package com.example.confirmationletter;
 
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
 
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.webflow.execution.RequestContext;
 
import com.example.dao.CurrencyDao;
import com.example.domain.AmountAndRecordsPerBank;
import com.example.domain.BatchTotal;
import com.example.domain.Client;
import com.example.domain.ConfirmationLetter;
import com.example.domain.Currency;
import com.example.domain.HashBatchRecordsBalance;
import com.example.domain.Record;
import com.example.record.command.FileUploadCommand;
import com.example.record.domain.TempRecord;
import com.example.record.parser.FileExtension;
import com.example.record.service.impl.Constants;
 
public class ConfirmationLetterGenerator {
 
  @SuppressWarnings("unused")
   private static Log logger = LogFactory.getLog(ConfirmationLetterGenerator.class);
  private static final String COLLECTIVE = "collectief";
 
  private String crediting;
  private String debiting;
  private String debit;
  private String credit;
  private String type;
  private LetterSelector letterSelector;
  private CurrencyDao currencyDao;
 
  public CurrencyDao getCurrencyDao() {
    return currencyDao;
  }
 
  public void setCurrencyDao(CurrencyDao currencyDao) {
    this.currencyDao = currencyDao;
  }
 
  public OurOwnByteArrayOutputStream letter(RequestContext context,
      FileUploadCommand fileUploadCommand, Client client,
      HashBatchRecordsBalance hashBatchRecordsBalance, String branchName,
      List<AmountAndRecordsPerBank> bankMap,
      List<com.example.record.domain.FaultRecord> faultyRecords,
      FileExtension extension, List<Record> records,
      List<TempRecord> faultyAccountNumberRecordList,
      List<TempRecord> sansDuplicateFaultRecordsList
  // ,
  // List<BigDecimal> retrievedAmountFL,
  // List<BigDecimal> retrievedAmountEUR,
  // List<BigDecimal> retrievedAmountUSD
  ) {
 
    ConfirmationLetter letter = new ConfirmationLetter();
    letter.setCurrency(records.get(0).getCurrency());
    letter.setExtension(extension);
 
    letter.setHashTotalCredit(hashBatchRecordsBalance.getHashTotalCredit()
        .toString());
    letter.setHashTotalDebit(hashBatchRecordsBalance.getHashTotalDebit()
        .toString());
 
    letter.setBatchTotalDebit(debitBatchTotal(
        hashBatchRecordsBalance.getBatchTotals(), client).toString());
    letter.setBatchTotalCredit(creditBatchTotal(
        hashBatchRecordsBalance.getBatchTotals(), client).toString());
 
    letter.setTotalProcessedRecords(hashBatchRecordsBalance
        .getRecordsTotal().toString());
    if (fileUploadCommand.getFee().equalsIgnoreCase(Constants.YES)) {
      letter.setTransactionCost(hashBatchRecordsBalance.getTotalFee()
          .toString());
    } else
      letter.setTransactionCost("");
    letter.setTransferType(hashBatchRecordsBalance.getCollectionType());
    // // logger.debug("letter method, bankMap: "+bankMap.size());
    letter.setBanks(bankMap);
 
    // uncommented this line
    letter.setCreditingErrors(faultyRecords);
    letter.setClient(client);
    letter.setBranchName(branchName);
    Map<String, BigDecimal> retrievedAmounts = new HashMap<String, BigDecimal>();
    retrievedAmounts = calculateRetrieveAmounts(records, faultyRecords,
        client, extension, faultyAccountNumberRecordList,
        sansDuplicateFaultRecordsList);
    letter.setRetrievedAmountEur(retrievedAmounts
        .get(Constants.CURRENCY_EURO));
    letter.setRetrievedAmountFL(retrievedAmounts
        .get(Constants.CURRENCY_FL));
    letter.setRetrievedAmountUsd(retrievedAmounts
        .get(Constants.CURRENCY_FL));
//    System.out.println("TRACING AMOUNT ["+letter.getRetrievedAmountFL()+"]");
    // letter.setRetrievedAmountFLDBF(retrievedAmounts.get("FLDBF"));
    // letter.setRetrievedAmountUSDDBF(retrievedAmounts.get("USDDBF"));
    // letter.setRetrievedAmountEURDBF(retrievedAmounts.get("EURDBF"));
    letter.setTotalRetrievedRecords(fileUploadCommand.getTotalRecords());
    OurOwnByteArrayOutputStream arrayOutputStream = letterSelector
        .generateLetter(client.getCreditDebit(), letter);
 
    context.getConversationScope().asMap().put("dsbByteArrayOutputStream",
        arrayOutputStream);
 
    return arrayOutputStream;
  }
 
  // Calculate sum amount from faultyAccountnumber list
 
  private Map<String, BigDecimal> calculateAmountsFaultyAccountNumber(
      List<TempRecord> faultyAccountNumberRecordList, Client client) {
    Map<String, BigDecimal> retrievedAmountsFaultyAccountNumber = new HashMap<String, BigDecimal>();
 
    BigDecimal faultyAccRecordAmountCreditFL = new BigDecimal(0);
    BigDecimal faultyAccRecordAmountCreditUSD = new BigDecimal(0);
    BigDecimal faultyAccRecordAmountCreditEUR = new BigDecimal(0);
 
    BigDecimal faultyAccRecordAmountDebitFL = new BigDecimal(0);
    BigDecimal faultyAccRecordAmountDebitUSD = new BigDecimal(0);
    BigDecimal faultyAccRecordAmountDebitEUR = new BigDecimal(0);
 
    for (TempRecord faultyAccountNumberRecord : faultyAccountNumberRecordList) {
      // // logger.debug("faultyAccountNumberRecord: "+
      // faultyAccountNumberRecord);
      // FL
      if (StringUtils.isBlank(faultyAccountNumberRecord.getSign())) {
        faultyAccountNumberRecord.setSign(client.getCreditDebit());
      }
 
      if (faultyAccountNumberRecord.getCurrencycode() == null) {
        String currencyId = currencyDao.retrieveCurrencyDefault(client
            .getProfile());
        Currency currency = currencyDao
            .retrieveCurrencyOnId(new Integer(currencyId));
        faultyAccountNumberRecord.setCurrencycode(currency.getCode()
            .toString());
      }
 
      if (faultyAccountNumberRecord.getCurrencycode().equals(
          Constants.FL_CURRENCY_CODE)
          || faultyAccountNumberRecord.getCurrencycode().equals(
              Constants.FL_CURRENCY_CODE_FOR_WEIRD_BANK)) {
 
        if (faultyAccountNumberRecord.getSign().equalsIgnoreCase(
            Constants.DEBIT)) {
          faultyAccRecordAmountDebitFL = new BigDecimal(
              faultyAccountNumberRecord.getAmount())
              .add(faultyAccRecordAmountDebitFL);
        } else {
          faultyAccRecordAmountCreditFL = new BigDecimal(
              faultyAccountNumberRecord.getAmount())
              .add(faultyAccRecordAmountCreditFL);
        }
      }
      if (faultyAccountNumberRecord.getCurrencycode().equals(
          Constants.USD_CURRENCY_CODE)) {
        if (faultyAccountNumberRecord.getSign().equalsIgnoreCase(
            Constants.DEBIT)) {
          faultyAccRecordAmountDebitUSD = new BigDecimal(
              faultyAccountNumberRecord.getAmount())
              .add(faultyAccRecordAmountDebitUSD);
        } else {
          faultyAccRecordAmountCreditUSD = new BigDecimal(
              faultyAccountNumberRecord.getAmount())
              .add(faultyAccRecordAmountCreditUSD);
        }
      }
      if (faultyAccountNumberRecord.getCurrencycode().equals(
          Constants.EUR_CURRENCY_CODE)) {
        if (faultyAccountNumberRecord.getSign().equalsIgnoreCase(
            Constants.DEBIT)) {
          faultyAccRecordAmountDebitEUR = new BigDecimal(
              faultyAccountNumberRecord.getAmount())
              .add(faultyAccRecordAmountDebitEUR);
        } else {
          faultyAccRecordAmountCreditEUR = new BigDecimal(
              faultyAccountNumberRecord.getAmount())
              .add(faultyAccRecordAmountCreditEUR);
        }
      }
 
      retrievedAmountsFaultyAccountNumber.put("FaultyAccDebitFL",
          faultyAccRecordAmountDebitFL);
      retrievedAmountsFaultyAccountNumber.put("FaultyAccDebitUSD",
          faultyAccRecordAmountDebitUSD);
      retrievedAmountsFaultyAccountNumber.put("FaultyAccDebitEUR",
          faultyAccRecordAmountDebitEUR);
 
      retrievedAmountsFaultyAccountNumber.put("FaultyAccCreditFL",
          faultyAccRecordAmountCreditFL);
      retrievedAmountsFaultyAccountNumber.put("FaultyAccCreditUSD",
          faultyAccRecordAmountCreditUSD);
      retrievedAmountsFaultyAccountNumber.put("FaultyAccCreditEUR",
          faultyAccRecordAmountCreditEUR);
 
    }
    return retrievedAmountsFaultyAccountNumber;
  }
 
  private Map<String, BigDecimal> calculateRetrieveAmounts(
      List<Record> records,
      List<com.example.record.domain.FaultRecord> faultyRecords,
      Client client, FileExtension extension,
      List<TempRecord> faultyAccountNumberRecordList,
      List<TempRecord> sansDuplicateFaultRecordsList) {
 
    Map<String, BigDecimal> retrievedAmounts = new HashMap<String, BigDecimal>();
 
    BigDecimal recordAmountFL = new BigDecimal(0);
    BigDecimal recordAmountUSD = new BigDecimal(0);
    BigDecimal recordAmountEUR = new BigDecimal(0);
 
    BigDecimal recordAmountDebitFL = new BigDecimal(0);
    BigDecimal recordAmountDebitEUR = new BigDecimal(0);
    BigDecimal recordAmountDebitUSD = new BigDecimal(0);
 
    BigDecimal recordAmountCreditFL = new BigDecimal(0);
    BigDecimal recordAmountCreditEUR = new BigDecimal(0);
    BigDecimal recordAmountCreditUSD = new BigDecimal(0);
 
    BigDecimal amountSansDebitFL = new BigDecimal(0);
    BigDecimal amountSansDebitUSD = new BigDecimal(0);
    BigDecimal amountSansDebitEUR = new BigDecimal(0);
 
    BigDecimal amountSansCreditFL = new BigDecimal(0);
    BigDecimal amountSansCreditUSD = new BigDecimal(0);
    BigDecimal amountSansCreditEUR = new BigDecimal(0);
 
    BigDecimal totalDebitFL = new BigDecimal(0);
    BigDecimal totalDebitUSD = new BigDecimal(0);
    BigDecimal totalDebitEUR = new BigDecimal(0);
 
    BigDecimal totalCreditFL = new BigDecimal(0);
    BigDecimal totalCreditUSD = new BigDecimal(0);
    BigDecimal totalCreditEUR = new BigDecimal(0);
 
    if (client.getCounterTransfer().equalsIgnoreCase(Constants.TRUE)) {
      for (Record record : records) {
        if (record.getFeeRecord() != 1) {
          if ((record.getCurrency().getCode().equals(
              Constants.FL_CURRENCY_CODE) || record
              .getCurrency().getCode().equals(
                  Constants.FL_CURRENCY_CODE_FOR_WEIRD_BANK))
              && record.getSign().equalsIgnoreCase(
                  Constants.DEBIT)) {
            recordAmountFL = record.getAmount().add(
                recordAmountFL);
            // system.out.println("recordAmountFL: ["+ recordAmountFL + "]");
 
          }
          if (record.getCurrency().getCode().equals(
              Constants.EUR_CURRENCY_CODE)
              && record.getSign().equalsIgnoreCase(
                  Constants.DEBIT)) {
            recordAmountEUR = record.getAmount().add(
                recordAmountEUR);
            // system.out.println("recordAmountEUR: ["+ recordAmountEUR + "]");
 
          }
          if (record.getCurrency().getCode().equals(
              Constants.USD_CURRENCY_CODE)
              && record.getSign().equalsIgnoreCase(
                  Constants.DEBIT)) {
            recordAmountUSD = record.getAmount().add(
                recordAmountUSD);
            // system.out.println("recordAmountUSD: ["+ recordAmountUSD + "]");
          }
        }
        retrievedAmounts.put(Constants.CURRENCY_EURO, recordAmountEUR);
        retrievedAmounts.put(Constants.CURRENCY_FL, recordAmountUSD);
        retrievedAmounts.put(Constants.CURRENCY_FL, recordAmountFL);
      }
    }
    // Not Balanced
    else {
 
      for (Record record : records) {
        logger.debug("COUNTERTRANSFER ["+record.getIsCounterTransferRecord()+"] FEERECORD ["+record.getFeeRecord()+"]");
        if (record.getIsCounterTransferRecord().compareTo(new Integer(0))==0
            && record.getFeeRecord().compareTo(new Integer(0))==0) {
          if ((record.getCurrency().getCode().equals(
              Constants.FL_CURRENCY_CODE) || record
              .getCurrency().getCode().equals(
                  Constants.FL_CURRENCY_CODE_FOR_WEIRD_BANK))) {
//            System.out.println("record to string: ["+record.toString()+"]");
            if (record.getSign().equalsIgnoreCase(Constants.DEBIT)) {
//               System.out.println("record.getamount DEBIT = ["+ record.getAmount() + "]");
              // system.out.println("recordAmountDebitFL 1 = "+ recordAmountDebitFL);
              recordAmountDebitFL = record.getAmount().add(
                  recordAmountDebitFL);
//              System.out.println("recordAmountDebitFL: ["+recordAmountDebitFL+"]");
            }
            if (record.getSign().equalsIgnoreCase(Constants.CREDIT)) {
//               System.out.println("record.getamount CREDIT = ["+record.getAmount()+"]");
              // system.out.println("recordAmountCreditFL 1 = ["+recordAmountCreditFL+"]");
 
              recordAmountCreditFL = record.getAmount().add(
                  recordAmountCreditFL);
//              System.out.println("recordAmountCreditFL: ["+recordAmountCreditFL+"]");
            }
 
            if (record.getCurrency().getCode().equals(
                Constants.EUR_CURRENCY_CODE)) {
 
              if (record.getSign().equalsIgnoreCase(
                  Constants.DEBIT)) {
                recordAmountDebitEUR = record.getAmount().add(
                    recordAmountDebitEUR);
                // system.out.println("recordAmountDebitEUR: ["+recordAmountDebitEUR+"]");
              }
              if (record.getSign().equalsIgnoreCase(
                  Constants.CREDIT)) {
                recordAmountCreditEUR = record.getAmount().add(
                    recordAmountCreditEUR);
                // system.out.println("recordAmountCreditEUR: ["+recordAmountCreditEUR+"]");
              }
 
            }
 
          }
        }
 
        if (record.getCurrency().getCode().equals(
            Constants.USD_CURRENCY_CODE)) {
 
          if (record.getSign().equalsIgnoreCase(Constants.DEBIT)) {
            recordAmountDebitUSD = record.getAmount().add(
                recordAmountDebitUSD);
            // system.out.println("recordAmountDebitUSD: ["+recordAmountDebitUSD+"]");
          }
          if (record.getSign().equalsIgnoreCase(Constants.CREDIT)) {
            recordAmountCreditUSD = record.getAmount().add(
                recordAmountCreditUSD);
            // system.out.println("recordAmountCreditUSD: ["+recordAmountCreditUSD+"]");
          }
 
        }
 
      }
      // Sansduplicate
      for (TempRecord sansDupRec : sansDuplicateFaultRecordsList) {
        // logger.debug("sansDupRec: "+sansDupRec);
        String currencyCode = sansDupRec.getCurrencycode();
        if (sansDupRec.getSign() == null) {
          String sign = client.getCreditDebit();
          sansDupRec.setSign(sign);
        }
        if (currencyCode == null) {
          String currencyId = currencyDao
              .retrieveCurrencyDefault(client.getProfile());
          Currency currency = currencyDao
              .retrieveCurrencyOnId(new Integer(currencyId));
          sansDupRec.setCurrencycode(currency.getCode().toString());
        } else {
 
          if (currencyCode.equals(Constants.FL_CURRENCY_CODE)
              || currencyCode
                  .equals(Constants.FL_CURRENCY_CODE_FOR_WEIRD_BANK)) {
 
            if (sansDupRec.getSign().equalsIgnoreCase(
                Constants.DEBIT)) {
              amountSansDebitFL = new BigDecimal(sansDupRec
                  .getAmount()).add(amountSansDebitFL);
            } else {
              amountSansCreditFL = new BigDecimal(sansDupRec
                  .getAmount()).add(amountSansCreditFL);
            }
          }
          if (currencyCode.equals(Constants.USD_CURRENCY_CODE)) {
            if (sansDupRec.getSign().equalsIgnoreCase(
                Constants.DEBIT)) {
              amountSansDebitUSD = new BigDecimal(sansDupRec
                  .getAmount()).add(amountSansDebitUSD);
            } else {
              amountSansCreditUSD = new BigDecimal(sansDupRec
                  .getAmount()).add(amountSansCreditUSD);
            }
          }
          if (currencyCode.equals(Constants.EUR_CURRENCY_CODE)) {
            if (sansDupRec.getSign().equalsIgnoreCase(
                Constants.DEBIT)) {
              amountSansDebitEUR = new BigDecimal(sansDupRec
                  .getAmount()).add(amountSansDebitEUR);
            } else {
              amountSansCreditEUR = new BigDecimal(sansDupRec
                  .getAmount()).add(amountSansCreditEUR);
            }
          }
        }
 
      }
 
      Map<String, BigDecimal> retrievedAccountNumberAmounts = calculateAmountsFaultyAccountNumber(
          faultyAccountNumberRecordList, client);
      // logger.info("Before total debit FL");
      // logger.info("amountSansDebitFL "+amountSansDebitFL);
      if (retrievedAccountNumberAmounts.get("FaultyAccDebitFL") != null
          && amountSansDebitFL != null) {
        // logger.info("retrievedAccountNumberAmounts.get(FaultyAccDebitFL) "+retrievedAccountNumberAmounts.get("FaultyAccDebitFL"));
        totalDebitFL = recordAmountDebitFL.add(amountSansDebitFL)
            .subtract(
                retrievedAccountNumberAmounts
                    .get("FaultyAccDebitFL"));
      } else if (amountSansDebitFL != null) {
        totalDebitFL = recordAmountDebitFL.add(amountSansDebitFL);
      } else {
        totalDebitFL = recordAmountDebitFL;
      }
      // logger.info("totalDebitFL "+totalDebitFL);
 
      if (retrievedAccountNumberAmounts.get("FaultyAccCreditFL") != null
          && amountSansCreditFL != null) {
        // logger.debug("retrievedAccountNumberAmounts.get(FaultyAccCreditFL):"+retrievedAccountNumberAmounts.get("FaultyAccCreditFL"));
        totalCreditFL = recordAmountCreditFL.add(amountSansCreditFL)
            .subtract(
                retrievedAccountNumberAmounts
                    .get("FaultyAccCreditFL"));
      } else if (amountSansCreditFL != null) {
        totalCreditFL = recordAmountCreditFL.add(amountSansCreditFL);
      } else {
        totalCreditFL = recordAmountCreditFL;
      }
      // logger.info("totalCreditFL: "+totalCreditFL);
 
      if (retrievedAccountNumberAmounts.get("FaultyAccDebitUSD") != null
          && amountSansDebitUSD != null) {
        // logger.info("retrievedAccountNumberAmounts.get(FaultyAccDebitUSD) "+retrievedAccountNumberAmounts.get("FaultyAccDebitUSD"));
        totalDebitUSD = recordAmountDebitUSD.add(amountSansDebitUSD)
            .subtract(
                retrievedAccountNumberAmounts
                    .get("FaultyAccDebitUSD"));
      } else if (amountSansDebitUSD != null) {
        totalDebitUSD = recordAmountDebitUSD.add(amountSansDebitUSD);
      } else {
        totalDebitUSD = recordAmountDebitUSD;
      }
      // logger.info("totalDebitUSD "+totalDebitUSD);
 
      if (retrievedAccountNumberAmounts.get("FaultyAccCreditUSD") != null
          && amountSansCreditUSD != null) {
        // logger.debug("retrievedAccountNumberAmounts.get(FaultyAccCreditUSD):"+retrievedAccountNumberAmounts.get("FaultyAccCreditUSD"));
        totalCreditUSD = recordAmountCreditUSD.add(amountSansCreditUSD)
            .subtract(
                retrievedAccountNumberAmounts
                    .get("FaultyAccCreditUSD"));
      } else if (amountSansCreditUSD != null) {
        totalCreditUSD = recordAmountCreditUSD.add(amountSansCreditUSD);
      } else {
        totalCreditUSD = recordAmountCreditUSD;
      }
      // logger.info("totalCreditUSD: "+totalCreditUSD);
 
      if (retrievedAccountNumberAmounts.get("FaultyAccDebitEUR") != null
          && amountSansDebitEUR != null) {
        // logger.info("retrievedAccountNumberAmounts.get(FaultyAccDebitEUR) "+retrievedAccountNumberAmounts.get("FaultyAccDebitEUR"));
        totalDebitEUR = recordAmountDebitEUR.add(amountSansDebitEUR)
            .subtract(
                retrievedAccountNumberAmounts
                    .get("FaultyAccDebitEUR"));
      } else if (amountSansDebitEUR != null) {
        totalDebitEUR = recordAmountDebitEUR.add(amountSansDebitEUR);
      } else {
        totalDebitEUR = recordAmountDebitEUR;
      }
      // logger.info("totalDebitEUR "+totalDebitEUR);
 
      if (retrievedAccountNumberAmounts.get("FaultyAccCreditEUR") != null
          && amountSansCreditEUR != null) {
        // logger.debug("retrievedAccountNumberAmounts.get(FaultyAccCreditEUR):"+retrievedAccountNumberAmounts.get("FaultyAccCreditEUR"));
        totalCreditEUR = recordAmountCreditEUR.add(amountSansCreditEUR)
            .subtract(
                retrievedAccountNumberAmounts
                    .get("FaultyAccCreditEUR"));
      } else if (amountSansCreditEUR != null) {
        totalCreditEUR = recordAmountCreditEUR.add(amountSansCreditEUR);
      } else {
        totalCreditEUR = recordAmountCreditEUR;
      }
      // logger.info("totalCreditEUR: "+totalCreditEUR);
 
      recordAmountFL = totalDebitFL.subtract(totalCreditFL).abs();
      recordAmountUSD = totalDebitUSD.subtract(totalCreditUSD).abs();
      recordAmountEUR = totalDebitEUR.subtract(totalCreditEUR).abs();
 
      retrievedAmounts.put(Constants.CURRENCY_EURO, recordAmountEUR);
      retrievedAmounts.put(Constants.CURRENCY_FL, recordAmountUSD);
      retrievedAmounts.put(Constants.CURRENCY_FL, recordAmountFL);
 
    }
 
    return retrievedAmounts;
  }
 
  private BigDecimal creditBatchTotal(Map<Integer, BatchTotal> batchTotals,
      Client client) {
    Double sum = new Double(0);
    Iterator<BatchTotal> itr = batchTotals.values().iterator();
    while (itr.hasNext()) {
      BatchTotal total = itr.next();
 
      sum = sum + total.getCreditValue().doubleValue();
    }
    Double d = sum / new Double(client.getAmountDivider());
    return new BigDecimal(d);
  }
 
  private BigDecimal debitBatchTotal(Map<Integer, BatchTotal> batchTotals,
      Client client) {
    Double sum = new Double(0);
    Iterator<BatchTotal> itr = batchTotals.values().iterator();
    while (itr.hasNext()) {
      BatchTotal total = itr.next();
      sum = sum + total.getCreditCounterValueForDebit().doubleValue();
    }
    Double d = sum / new Double(client.getAmountDivider());
    return new BigDecimal(d);
  }
 
  private List<AmountAndRecordsPerBank> amountAndRecords(
      List<Record> records, String transactionType) {
    List<AmountAndRecordsPerBank> list = new ArrayList<AmountAndRecordsPerBank>();
    String typeOfTransaction = transactionType.equalsIgnoreCase(crediting) ? crediting
        : debiting;
    type = typeOfTransaction.equalsIgnoreCase(crediting) ? credit : debit;
    if (transactionType.equalsIgnoreCase(typeOfTransaction)) {
      for (Record record : records) {
        getAmountAndRecords(record, list, transactionType);
      }
    }
    return list;
  }
 
  private List<AmountAndRecordsPerBank> getAmountAndRecords(Record record,
      List<AmountAndRecordsPerBank> list, String transactionType) {
    Map<String, String> map = new HashMap<String, String>();
    if (record.getFeeRecord().compareTo(0) == 0
        && !map.containsKey(record.getBeneficiaryName())) {
 
      if (transactionType.equalsIgnoreCase(Constants.CREDITING)) {
 
        if (record.getBeneficiaryName() != null
            && !record.getBeneficiaryName().equalsIgnoreCase(
                Constants.RBTT_BANK_ALTERNATE)) {
          Boolean newList = true;
          if (list.size() == 0
              && record.getSign().equalsIgnoreCase(type)) {
            // logger.info("bank gegevens: "+record.getSign()+" : "+record.getBank().getName()+" : "+record.getBeneficiaryName());
            AmountAndRecordsPerBank aARPB = new AmountAndRecordsPerBank();
            aARPB.setBankName(record.getBank().getName());
            aARPB.setTotalRecord(1);
            aARPB.setAmount(record.getAmount());
            aARPB.setCurrencyType(record.getCurrency()
                .getCurrencyType());
            aARPB.setAccountNumber(record
                .getBeneficiaryAccountNumber());
            list.add(aARPB);
            newList = false;
          }
          if (newList && record.getSign().equalsIgnoreCase(type)) {
            // logger.info("bank gegevens: "+record.getSign()+" : "+record.getBank().getName()+" : "+record.getBeneficiaryName());
            Boolean newRecord = true;
            for (AmountAndRecordsPerBank object : list) {
              if (object.getBankName().equalsIgnoreCase(
                  record.getBank().getName())
                  && object.getCurrencyType()
                      .equalsIgnoreCase(
                          record.getCurrency()
                              .getCurrencyType())) {
                object.setAmount(object.getAmount().add(
                    record.getAmount()));
                object
                    .setTotalRecord(object.getTotalRecord() + 1);
                newRecord = false;
              }
            }
            if (newRecord) {
              AmountAndRecordsPerBank aARPB = new AmountAndRecordsPerBank();
              aARPB.setBankName(record.getBank().getName());
              aARPB.setTotalRecord(1);
              aARPB.setAmount(record.getAmount());
              aARPB.setCurrencyType(record.getCurrency()
                  .getCurrencyType());
              aARPB.setAccountNumber(record
                  .getBeneficiaryAccountNumber());
              list.add(aARPB);
            }
          }
        }
      }
 
      // del begin
      if (transactionType.equalsIgnoreCase(Constants.DEBITING)) {
 
        if (record.getBeneficiaryName() == null) {
          Boolean newList = true;
          if (list.size() == 0
              && record.getSign().equalsIgnoreCase(type)) {
            // logger.info("bank gegevens: "+record.getSign()+" : "+record.getBank().getName()+" : "+record.getBeneficiaryName());
            AmountAndRecordsPerBank aARPB = new AmountAndRecordsPerBank();
            aARPB.setBankName(record.getBank().getName());
            aARPB.setTotalRecord(1);
            aARPB.setAmount(record.getAmount());
            aARPB.setCurrencyType(record.getCurrency()
                .getCurrencyType());
            aARPB.setAccountNumber(record
                .getBeneficiaryAccountNumber());
            list.add(aARPB);
            newList = false;
          }
          if (newList && record.getSign().equalsIgnoreCase(type)) {
            // logger.info("bank gegevens: "+record.getSign()+" : "+record.getBank().getName()+" : "+record.getBeneficiaryName());
            Boolean newRecord = true;
            for (AmountAndRecordsPerBank object : list) {
              if (object.getBankName().equalsIgnoreCase(
                  record.getBank().getName())
                  && object.getCurrencyType()
                      .equalsIgnoreCase(
                          record.getCurrency()
                              .getCurrencyType())) {
                object.setAmount(object.getAmount().add(
                    record.getAmount()));
                object
                    .setTotalRecord(object.getTotalRecord() + 1);
                newRecord = false;
              }
            }
            if (newRecord) {
              AmountAndRecordsPerBank aARPB = new AmountAndRecordsPerBank();
              aARPB.setBankName(record.getBank().getName());
              aARPB.setTotalRecord(1);
              aARPB.setAmount(record.getAmount());
              aARPB.setCurrencyType(record.getCurrency()
                  .getCurrencyType());
              aARPB.setAccountNumber(record
                  .getBeneficiaryAccountNumber());
              list.add(aARPB);
            }
          }
        }
      }
      // del end
    }
    return list;
  }
 
  /*
   *
   * Getters and setters
   */
 
  public void setCrediting(String crediting) {
    this.crediting = crediting;
  }
 
  public void setDebiting(String debiting) {
    this.debiting = debiting;
  }
 
  public void setDebit(String debit) {
    this.debit = debit;
  }
 
  public void setCredit(String credit) {
    this.credit = credit;
  }
 
  public void setLetterSelector(LetterSelector letterSelector) {
    this.letterSelector = letterSelector;
  }
 
}
