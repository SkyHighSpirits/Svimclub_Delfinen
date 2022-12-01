import java.util.ArrayList;

public class CompetitiveSwimmer extends Member
{
    // creating variables
    private String trainer;
    private boolean isCrawl;
    private boolean isBrystSvoemning;
    private boolean isButterfly;
    private boolean isRygcrawl;


    // creating constructors
    public CompetitiveSwimmer(String cpr, AgeGroup ageGroup, boolean membershipStatus, int subscriptionFee, int debt, int age, String trainer) {
        super(cpr, ageGroup, membershipStatus, subscriptionFee, debt, age);
        this.trainer = trainer;
    }

    //constructor for when created from database
    public CompetitiveSwimmer(String swimmerType, String cpr, AgeGroup ageGroup, boolean membershipStatus, int subscriptionFee, int debt, int age, double crawlResultat, double brystsoevmningResultat, double butterflyResultat,double rygCrawlResultat, String trainer, boolean isCrawl, boolean isBrystSvoemning, boolean isButterfly, boolean isRygcrawl)
    {
        super(swimmerType, cpr, ageGroup, membershipStatus, subscriptionFee, debt, age, crawlResultat, brystsoevmningResultat, butterflyResultat, rygCrawlResultat);
        this.trainer = trainer;
        this.isCrawl = isCrawl;
        this.isBrystSvoemning = isBrystSvoemning;
        this.isButterfly = isButterfly;
        this.isRygcrawl = isRygcrawl;
    }

}
