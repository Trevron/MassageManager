package trevron.utility;

public class BillFormatter {

    public static String format(String name, int cost, int appointments, String month) {
        String bill =
                  "---------------------------------------------------------------------------------------\n"
                + "---------------------------------------------------------------------------------------\n\n\n"
                + "INVOICE\n\n"
                + "Customer : " + name + "\n"
                + "Month : " + month + "\n"
                + "Total number of appointments : " + appointments + "\n"
                + "Total cost : $" + cost + "\n\n\n"
                + "---------------------------------------------------------------------------------------\n"
                + "---------------------------------------------------------------------------------------\n";

        return bill;
    }

}
