public class WorkoutSwimmer extends Member
{
    public WorkoutSwimmer(String swimmerType, String cpr, AgeGroup ageGroup, boolean membershipStatus, int subscriptionFee, int debt, int age) {
        super(swimmerType, cpr, ageGroup, membershipStatus, subscriptionFee, debt, age);
    }


    // constructor to create from database

    public String toString()
    {
        return "\nSwimmerType: " + super.getSwimmerType() + "\nCPR: " + super.getCpr() + "\nAgeGroup: " + super.getAgeGroup() + "\nMembershipStatus: " + super.getMembershipStatus() + "\nSubscriptionFee: " + super.getSubscriptionFee() + "\nDebt: " + super.getDebt() + "\nAge: " + getAge();
    }
}

