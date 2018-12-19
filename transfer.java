public class UglyMoneyTransferService 
{
  public void transferFunds(Account source, Account target, BigDecimal amount, boolean allowDuplicateTxn){
    Account sourceAccount = null;
    if(rs.next()) {
      sourceAccount = new Account();
      //populate account
    }
    Account targetAccount = null;
    if(!sourceAccount.isOverdraftAllowed()) {
      if((sourceAccount.getBalance() - amount) < 0) {
        throw new RuntimeException("Insufficient Balance");
      }
    }
    else {
      if(((sourceAccount.getBalance()+sourceAccount.getOverdraftLimit()) - amount) < 0) {
        throw new RuntimeException("Insufficient Balance, Exceeding Overdraft Limit");
      }
    }
    AccountTransaction lastTxn = .. ; //JDBC code to obtain last transaction of sourceAccount
    if(lastTxn != null) {
      if(lastTxn.getTargetAcno().equals(targetAccount.getAcno()) && lastTxn.getAmount() == amount && !allowDuplicateTxn) {
        throw new RuntimeException("Duplicate transaction exception");
      }
    }
    sourceAccount.debit(amount);
    targetAccount.credit(amount);
  }

}
