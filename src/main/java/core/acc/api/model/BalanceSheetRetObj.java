package core.acc.api.model;

public class BalanceSheetRetObj {

    private double fixedAss;
    private double currentAss;
    private double capital;
    private double liability;
    private double profit;
    private double retailed;

    public BalanceSheetRetObj() {
        fixedAss = 0.0;
        currentAss = 0.0;
        capital = 0.0;
        liability = 0.0;
        profit = 0.0;
    }

    public double getFixedAss() {
        return fixedAss;
    }

    public void setFixedAss(double fixedAss) {
        this.fixedAss += fixedAss;
    }

    public double getCurrentAss() {
        return currentAss;
    }

    public void setCurrentAss(double currentAss) {
        this.currentAss += currentAss;
    }

    public double getCapital() {
        return capital;
    }

    public void setCapital(double capital) {
        this.capital += capital;
    }

    public double getLiability() {
        return liability;
    }

    public void setLiability(double liability) {
        this.liability += liability;
    }

    public double getProfit() {
        return profit;
    }

    public void setProfit(double profit) {
        this.profit += profit;
    }

    public double getTotalAss() {
        return fixedAss + currentAss;
    }

    public double getTotalCapital() {
        return liability + capital;
    }

    public double getRetailed() {
        return retailed;
    }

    public void setRetailed(double retailed) {
        this.retailed = retailed;
    }

    public double getPlTotal() {
        return getCapital() + getLiability() + getProfit() + getRetailed();
    }
}
