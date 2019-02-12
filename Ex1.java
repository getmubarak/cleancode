 public class StatusCategorizer {
private static final String NOT_RECONCILED = "NOT_RECONCILED";
    private static final String NOT_RECONCILED_NO_SALE = "NOT_RECONCILED_NO_SALE";
    private static final String NOT_RECONCILED_NO_TENDER = "NOT_RECONCILED_NO_TENDER";
    private static final String NOT_RECONCILED_NO_SALE_TENDER = "NOT_RECONCILED_NO_SALE_TENDER";
    private static final String RECONCILED_OPT_TILLS = "RECONCILED_OPT_TILLS";
    private static final String RECONCILED_FUEL_ROUNDING_ERROR = "RECONCILED_FUEL_ROUNDING_ERROR";
    private static final String RECONCILED_NON_FUEL_ROUNDING_ERROR = "RECONCILED_NON_FUEL_ROUNDING_ERROR";

    public static String categorize(String tillId, BigDecimal difference, Sale sale, List<Tender> tenders) {
        String status = difference.compareTo(ZERO) == 0 ? "RECONCILED" : "NOT_RECONCILED";

        if (noSaleCheck(sale) && !noTenderCheck(tenders)) {
            return NOT_RECONCILED_NO_SALE;
        }
        else if (noTenderCheck(tenders) && !noSaleCheck(sale)) {
            return NOT_RECONCILED_NO_TENDER;
        }
        else if (noSaleCheck(sale) && noTenderCheck(tenders)) {
            return NOT_RECONCILED_NO_SALE_TENDER;
        }
        else if (optTills(tillId, status)) {
            return RECONCILED_OPT_TILLS;
        }
        else if (fuelRoundingError(status, tillId, difference)) {
            return RECONCILED_FUEL_ROUNDING_ERROR;
        }
        else if (nonFuelRoundingError(status, tillId, difference)) {
            return RECONCILED_NON_FUEL_ROUNDING_ERROR;
        }
        return status;
    }
