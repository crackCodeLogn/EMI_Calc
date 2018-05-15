import java.text.DecimalFormat;
import java.util.Scanner;

/**
 * @author Vivek
 * @version 1.0
 * @since 15-05-2018
 * <p>
 * This program calculates the EMI to be paid according to the principal amount, rate of interest (yearly) and the number of months for the installments.
 * Then it generates the installment schedule, with details such as
 * -> Principal at beginning of that installment
 * -> Amount to be paid as interest
 * -> Principal repayment being done with that installment
 * -> Outstanding principal amount at the end of that installment
 * <p>
 * Note: As Java (or any other programming lng) is being used, precision of the results may vary, but the effect is not going to be too big.
 */
public class EMI_Calculator {

    /**
     * This internal class stores the entire EMI schedule which is displayed in the end, for all the months
     */
    private static class EMI_Schedule {
        int months;
        double emi[], principal_outstanding_at_beginning[], interest[], principal_repayment[], balance_outstanding_at_end[];

        public EMI_Schedule(int numberOfMonths) {
            months = numberOfMonths;
            emi = new double[numberOfMonths];
            principal_outstanding_at_beginning = new double[numberOfMonths];
            interest = new double[numberOfMonths];
            principal_repayment = new double[numberOfMonths];
            balance_outstanding_at_end = new double[numberOfMonths];
        }

        public int getMonths() {
            return months;
        }

        public double[] getEmi() {
            return emi;
        }

        public void setEmi(double[] emi) {
            this.emi = emi;
        }

        public double[] getPrincipal_outstanding_at_beginning() {
            return principal_outstanding_at_beginning;
        }

        public void setPrincipal_outstanding_at_beginning(double[] principal_outstanding_at_beginning) {
            this.principal_outstanding_at_beginning = principal_outstanding_at_beginning;
        }

        public double[] getInterest() {
            return interest;
        }

        public void setInterest(double[] interest) {
            this.interest = interest;
        }

        public double[] getPrincipal_repayment() {
            return principal_repayment;
        }

        public void setPrincipal_repayment(double[] principal_repayment) {
            this.principal_repayment = principal_repayment;
        }

        public double[] getBalance_outstanding_at_end() {
            return balance_outstanding_at_end;
        }

        public void setBalance_outstanding_at_end(double[] balance_outstanding_at_end) {
            this.balance_outstanding_at_end = balance_outstanding_at_end;
        }
    }

    /**
     * This function calculates the total amount to be repaid.
     *
     * @param principal
     * @param rate
     * @param months
     * @return AMOUNT
     */
    private static double calcTotalAmount(double principal, double rate, int months) {
        double amt = principal;
        rate /= 12;
        amt *= Math.pow((1 + rate / 100), months);
        amt = formatter_DoubleRet(amt);
        return amt;
    }

    /**
     * This function calculates the EMI to be paid each month.
     *
     * @param principal
     * @param rate
     * @param months
     * @return EMI
     */
    private static double calcEMI(double principal, double rate, int months) {
        double emi;
        rate /= 1200;
        emi = (principal * rate * Math.pow(1 + rate, months)) / (Math.pow(1 + rate, months) - 1);
        emi = Math.round(emi * 1000.0) / 1000.0;
        return emi;
    }

    /**
     * This function computes the entire EMi repayment schdule.
     *
     * @param EMI
     * @param principal
     * @param rate
     * @param emi_schedule
     * @return EMI-SCHEDULE
     */
    private static EMI_Schedule computeEMI_Schedule(double EMI, double principal, double rate, EMI_Schedule emi_schedule) {
        int i, months = emi_schedule.getMonths();
        double emi[], prin_begin[], interest[], prin_repay[], outbal_end[];
        emi = emi_schedule.getEmi();
        prin_begin = emi_schedule.getPrincipal_outstanding_at_beginning();
        interest = emi_schedule.getInterest();
        prin_repay = emi_schedule.getPrincipal_repayment();
        outbal_end = emi_schedule.getBalance_outstanding_at_end();
        rate /= 1200;

        for (i = 0; i < months; i++) {
            emi[i] = EMI;
            if (i == 0) prin_begin[i] = principal;
            else prin_begin[i] = outbal_end[i - 1];

            /*
            //original
            interest[i] = prin_begin[i] * rate;
            prin_repay[i] = emi[i] - interest[i];
            outbal_end[i] = prin_begin[i] - prin_repay[i];
            */

            //original with slight modification, but still not good
            //interest[i] = Math.round(prin_begin[i] * rate * 100.0) / 100.0;
            interest[i] = formatter_DoubleRet(prin_begin[i] * rate);
            prin_repay[i] = emi[i] - interest[i];
            outbal_end[i] = prin_begin[i] - prin_repay[i];

            /*
            //almost good working
            interest[i] = Double.parseDouble(new DecimalFormat("##.00").format(prin_begin[i] * rate));
            prin_repay[i] = Double.parseDouble(new DecimalFormat("##.00").format(emi[i] - interest[i]));
            outbal_end[i] = Double.parseDouble(new DecimalFormat("##.00").format(prin_begin[i] - prin_repay[i]));
            */

            /*
            //almost better working than the original - making use of rounding function
            interest[i] = Math.round(prin_begin[i] * rate * 100.0) / 100.0;
            prin_repay[i] = Math.round((emi[i] - interest[i]) * 100.0) / 100.0;
            outbal_end[i] = Math.round((prin_begin[i] - prin_repay[i]) * 100.0) / 100.0;
            */
        }

        for (i = 0; i < months; i++) {
            emi[i] = formatter_DoubleRet(emi[i]);
            prin_begin[i] = formatter_DoubleRet(prin_begin[i]);
            interest[i] = formatter_DoubleRet(interest[i]);
            prin_repay[i] = formatter_DoubleRet(prin_repay[i]);
            outbal_end[i] = formatter_DoubleRet(outbal_end[i]);
        }

        emi_schedule.setEmi(emi);
        emi_schedule.setPrincipal_outstanding_at_beginning(prin_begin);
        emi_schedule.setInterest(interest);
        emi_schedule.setPrincipal_repayment(prin_repay);
        emi_schedule.setBalance_outstanding_at_end(outbal_end);
        return emi_schedule;
    }

    /**
     * This function is used for setting the precision of decimal / floating point variables to 2.
     *
     * @param data
     * @return precise floating point value
     */
    private static double formatter_DoubleRet(double data) {
        return Math.round(data * 100.0) / 100.0;
    }

    /**
     * This function is used for formatting an input floating point variable to a String, with precision ~ 2.
     *
     * @param data
     * @return precise String
     */
    private static String formatter_StringRet(double data) {
        String internal = Double.toString(data);
        String prePart = internal.substring(0, internal.indexOf('.'));
        internal = internal.substring(internal.indexOf('.') + 1);

        if (internal.length() == 1) {
            System.out.println("\tinternal len 1 : " + data + ", internal : " + internal);
            internal = prePart + "." + internal + "0";
        } else if (internal.length() == 2) {
            internal = prePart + "." + internal;
        } else if (internal.length() > 2) {
            internal = prePart + "." + internal.substring(0, 2);
        }
        return internal;
    }

    /**
     * The main function, where everything happens.
     *
     * @param args
     */
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        double principal, rate, EMI, AMOUNT;
        int months;

        System.out.println("Enter the details:-");
        System.out.print("Enter the principal amount : ₹");
        principal = in.nextDouble();
        System.out.print("Enter the rate of interest (yearly) : ");
        rate = in.nextDouble();
        System.out.print("Enter the time in months : ");
        months = in.nextInt();

        System.out.print("Total amount : ₹");
        //System.out.println(calcTotalAmount(principal, calcCompoundInterest(principal, rate, months)));
        System.out.println(calcTotalAmount(principal, rate, months));

        System.out.print("EMI : ₹");
        EMI = calcEMI(principal, rate, months);
        System.out.println(formatter_DoubleRet(EMI));

        //shifting over to making the schedule for the EMI payment
        EMI_Schedule emi_schedule = new EMI_Schedule(months);
        emi_schedule = computeEMI_Schedule(EMI, principal, rate, emi_schedule);

        System.out.println("Number\tEMI\t\tPrincipal at beginning\tInterest\tPrincipal Repayment\t\tOutstanding at end");
        for (int i = 0; i < months; i++) {
            System.out.println((i + 1) +
                    "\t\t₹" + emi_schedule.getEmi()[i] +
                    "\t₹" + emi_schedule.getPrincipal_outstanding_at_beginning()[i] +
                    "\t\t\t₹" + emi_schedule.getInterest()[i] +
                    "\t\t\t₹" + emi_schedule.getPrincipal_repayment()[i] +
                    "\t\t\t₹" + emi_schedule.getBalance_outstanding_at_end()[i]);
        }
    }
}
