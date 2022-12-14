import java.io.*;
import java.security.cert.CollectionCertStoreParameters;
import java.sql.SQLOutput;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.time.*;
import java.util.Collections;
import java.util.Comparator;

public class MemberHandler implements MemberHandlerInterface
{
    private ArrayList<Member> memberList;

    private UserData userData;
    private ArrayList<StaevneResultat> staevneResultatList;

    public MemberHandler() throws FileNotFoundException {
        this.memberList = new ArrayList<>();
        this.staevneResultatList = new ArrayList<>();
        this.userData = new UserData(memberList, staevneResultatList);
        userData.bootUserData();
    }


    public UserData getUserData() {
        return userData;
    }
    public void CreateMember()
    {
        OutputHandler.printTextBoxStart();
        System.out.println("Select either Workout Swimmer [1] or competitive swimmer [2]");
        int selection = selectMembershipType();
        OutputHandler.printTextBoxEnd();
        if(selection == 1)
        {
            OutputHandler.printTextBoxStart();
            String cpr = InputHandler.inputCPR();
            OutputHandler.printTextBoxEnd();
            AgeGroup ageGroup = findAgeGroup(cpr);
            OutputHandler.printInputBool();
            boolean active = InputHandler.fromInputToBool();
            int fee = calculateSubscriptionFee(ageGroup, cpr, active);
            int debt = 0;
            int age = castCPRToAge(cpr);
            WorkoutSwimmer workoutSwimmer = new WorkoutSwimmer("WorkoutSwimmer", cpr, ageGroup, active, fee, debt, age);
            addToMemberList(workoutSwimmer);
            userData.outputToMemberDatabase();
        }
        else if(selection == 2)
        {
            OutputHandler.printTextBoxStart();
            String cpr = InputHandler.inputCPR();
            OutputHandler.printTextBoxEnd();
            AgeGroup ageGroup = findAgeGroup(cpr);
            OutputHandler.printInputBool();
            boolean active = InputHandler.fromInputToBool();
            int fee = calculateSubscriptionFee(ageGroup, cpr, active);
            int debt = 0;
            int age = castCPRToAge(cpr);
            System.out.println("Which trainer have you signed up for [Martin] eller [Christina], skriv navn");
            String trainer = InputHandler.inputString();
            CompetitiveSwimmer competitiveSwimmer = new CompetitiveSwimmer("CompetitiveSwimmer", cpr, ageGroup, active, fee, debt, age, trainer);
            addToMemberList(competitiveSwimmer);
            userData.outputToMemberDatabase();
        }

    }

    public int castCPRToAge(String cpr)
    {
        LocalDate dob = convertCprToLocalDate(cpr);
        int age = findAge(dob);
        return age;
    }

    public AgeGroup findAgeGroup(String cpr)
    {
        AgeGroup group;
        LocalDate dob = convertCprToLocalDate(cpr);

        int age = findAge(dob);
        System.out.println("You are: " + age);
        if(age >= 18)
        {
            group = AgeGroup.Senior;
        }
        else
        {
            group = AgeGroup.Junior;
        }
        System.out.println("You are: " + group);
        return group;
    }

    public int findAge(LocalDate dob)
    {
        int age = (int) ChronoUnit.YEARS.between(dob, LocalDate.now());
        return age;
    }

    public LocalDate convertCprToLocalDate(String cpr)
    {
        //splits cpr into day month year format by accessing the index from and to where they appear
        int day =  Integer.parseInt(cpr.substring(0, 2));
        int month = Integer.parseInt(cpr.substring(2, 4));
        int year = Integer.parseInt(cpr.substring(4, 8));
        //Assembles date format of the cpr YYYY-MM-DD
        LocalDate dob = LocalDate.of(year, month, day);
        return dob;
    }

    public Member findMember()
    {
        Member member;
        String cpr = InputHandler.inputCPR();

        for(int i=0; i<memberList.size(); i++)
        {
            if (memberList.get(i).getCpr().equals(cpr))
            {
                member = memberList.get(i);
                return member;
            }
        }
        OutputHandler.printErrorUserNotFound();
        return null;
    }
    public void PrintMemberInformation()
    {
        Member member = findMember();
        if(member instanceof CompetitiveSwimmer)
        {
            String information = ((CompetitiveSwimmer) member).toString();
            OutputHandler.printMemberInformation(information);
        }
        else if (member instanceof WorkoutSwimmer)
        {
            String information = ((WorkoutSwimmer) member).toString();
            OutputHandler.printMemberInformation(information);
        }
    }

    public void UpdateMemberType()
    {


        OutputHandler.printTextBoxStart();
        OutputHandler.printPleaseEnterCpr();
        OutputHandler.printTextBoxEnd();
        Member member = findMember();
        System.out.println("Is Swimmer a Competitor[1] or a Workout[2] swimmer?");
        int selection = InputHandler.inputInt();
        if(selection == 1)
        {
            member.setSwimmerType("CompetitiveSwimmer");
        }
        if(selection == 2)
        {
            member.setSwimmerType("WorkoutSwimmer");
        }
        userData.outputToMemberDatabase();
    }
    public void UpdateMemberAgeGroup()
    {
        OutputHandler.printTextBoxStart();
        OutputHandler.printPleaseEnterCpr();
        OutputHandler.printTextBoxEnd();
        Member member = findMember();
        OutputHandler.printTextBoxStart();
        OutputHandler.printPleaseEnterUpdatedCpr();
        OutputHandler.printTextBoxEnd();
        String cpr = InputHandler.inputCPR();
        AgeGroup ageGroup = findAgeGroup(cpr);
        member.setAgeGroup(ageGroup);
        member.setCpr(cpr);
        userData.outputToMemberDatabase();
    }
    public void UpdateMemberShipStatus()
    {

        OutputHandler.printTextBoxStart();
        OutputHandler.printPleaseEnterCpr();
        OutputHandler.printTextBoxEnd();
        Member member = findMember();
        OutputHandler.printInputBool();
        member.setMembershipStatus(InputHandler.fromInputToBool());
        userData.outputToMemberDatabase();
    }

    public int selectMembershipType()
    {
        int memberShipChoice = InputHandler.inputMenuChoice(2);
        return memberShipChoice;
    }

    public void chargeMembers()
    {
        for(Member member : memberList)
        {
            member.addDebt(calculateSubscriptionFee(member.getAgeGroup(),member.getCpr(), member.getMembershipStatus()));
        }
        OutputHandler.printCharged();
        userData.outputToMemberDatabase();
    }

    public int calculateSubscriptionFee(AgeGroup ageGroup, String cpr, boolean active)
    {
        int fee = 0;
        int age = castCPRToAge(cpr);

        if(active == true && ageGroup == AgeGroup.Junior)
        {
            fee = fee + 1000;
        }
        else if(active == true && ageGroup == AgeGroup.Senior)
        {
            fee = fee + 1600;
        }
        else if(active == true && age >= 60)
        {
            fee = fee + 1200;
        }
        else if(active == false)
        {
           fee = fee + 500;
        }
        return fee;
    }

    public void getBestSwimmerData()
    {
        userData.fetchBestCrawlResultat();
        OutputHandler.printString("\n");
        userData.fetchBestButterflyResultat();
        OutputHandler.printString("\n");
        userData.fetchBestRygCrawlResultat();
        OutputHandler.printString("\n");
        userData.fetchBestBrystsvoemning();
        OutputHandler.printString("\n");
    }

    public void updateSwimDisciplineCrawl()
    {
        String cpr = InputHandler.inputCPR();

        for(Member member : memberList)
        {
            if(member instanceof CompetitiveSwimmer)
            {
                if (member.getCpr().equals(cpr))
                {
                    if(((CompetitiveSwimmer) member).getIsCrawl() == true)
                    {
                        ((CompetitiveSwimmer) member).setCrawl(false);
                        OutputHandler.printChargedToFalse();
                    } else if (((CompetitiveSwimmer) member).getIsCrawl() == false)
                    {
                        ((CompetitiveSwimmer) member).setCrawl(true);
                        OutputHandler.printChargedToTrue();
                    }
                }
            }
        }
        userData.outputToMemberDatabase();
    }
    public void updateSwimDisciplineBrystSvoemning()
    {
        String cpr = InputHandler.inputCPR();

        for(Member member : memberList)
        {
            if(member instanceof CompetitiveSwimmer)
            {
                if(member.getCpr().equals(cpr))
                {
                    if(((CompetitiveSwimmer) member).getIsBrystSvoemning() == true)
                    {
                        ((CompetitiveSwimmer) member).setBrystSvoemning(false);
                        OutputHandler.printChargedToFalse();
                    }else if (((CompetitiveSwimmer) member).getIsBrystSvoemning() == false)
                    {
                        ((CompetitiveSwimmer) member).setBrystSvoemning(true);
                        OutputHandler.printChargedToTrue();
                    }
                }
            }
        }
        userData.outputToMemberDatabase();
    }
    public void updateSwimDisciplineButterflySvoemning()
    {
        String cpr = InputHandler.inputCPR();

        for(Member member : memberList)
        {
            if(member instanceof CompetitiveSwimmer)
            {
                if(member.getCpr().equals(cpr))
                {
                    if(((CompetitiveSwimmer) member).getIsButterfly() == true)
                    {
                        ((CompetitiveSwimmer) member).setButterfly(false);
                        OutputHandler.printChargedToFalse();
                    }else if (((CompetitiveSwimmer) member).getIsButterfly() == false)
                    {
                        ((CompetitiveSwimmer) member).setButterfly(true);
                        OutputHandler.printChargedToTrue();
                    }
                }
            }
        }
        userData.outputToMemberDatabase();
    }

    public void updateSwimDisciplineRygcrawl()
    {
        String cpr = InputHandler.inputCPR();

        for(Member member : memberList)
        {
            if(member instanceof CompetitiveSwimmer)
            {
                if(member.getCpr().equals(cpr))
                {
                    if(((CompetitiveSwimmer) member).getIsRygcrawl() == true)
                    {
                        ((CompetitiveSwimmer) member).setRygcrawl(false);
                        OutputHandler.printChargedToFalse();
                    }else if (((CompetitiveSwimmer) member).getIsRygcrawl() == false)
                    {
                        ((CompetitiveSwimmer) member).setRygcrawl(true);
                        OutputHandler.printChargedToTrue();
                    }
                }
            }
        }
        userData.outputToMemberDatabase();
    }
    public void payDebt()
    {
        Member member = findMember();
        if(member != null)
        {
            OutputHandler.printWishToPay();
            OutputHandler.printPersonDebt(member.getDebt());
            boolean payBoolean = InputHandler.inputPayDebt();
            if(payBoolean)
            {
                member.setDebt(0);
                OutputHandler.printPersonDebt(member.getDebt());
                OutputHandler.printThankPayment();
            }
            else
            {
                OutputHandler.printGoodDay();
                OutputHandler.printTextBoxEnd();
            }
            userData.outputToMemberDatabase();
        }
    }

    public void updateTrainingTimeCrawl()
    {
        Member member = findMember();
        if(member instanceof CompetitiveSwimmer)
        {
            OutputHandler.printPleaseEnterTime();
            double input = InputHandler.inputDouble();
            if(input < ((CompetitiveSwimmer) member).getCrawlResultat() || ((CompetitiveSwimmer) member).getCrawlResultat() == 0)
            {
                ((CompetitiveSwimmer) member).setCrawlResultat(input);
                OutputHandler.printString("User Crawl time was changed to: " + ((CompetitiveSwimmer) member).getCrawlResultat());
            }
            else
            {
                OutputHandler.printErrorResultNotBetter();
            }

        }
        else
        {
            OutputHandler.printErrorUserType();
        }
        userData.outputToMemberDatabase();
    }
    public void updateTrainingTimeButterfly()
    {
        Member member = findMember();
        if(member instanceof CompetitiveSwimmer)
        {
            OutputHandler.printPleaseEnterTime();
            double input = InputHandler.inputDouble();
            if(input < ((CompetitiveSwimmer) member).getButterflyResultat() || ((CompetitiveSwimmer) member).getButterflyResultat() == 0)
            {
                ((CompetitiveSwimmer) member).setButterflyResultat(input);
                OutputHandler.printString("User Butterfly time was changed to: " + ((CompetitiveSwimmer) member).getButterflyResultat());

            }
            else
            {
                OutputHandler.printErrorResultNotBetter();
            }
        }
        else
        {
            OutputHandler.printErrorUserType();
        }
        userData.outputToMemberDatabase();
    }
    public void updateTrainingTimeBryst()
    {
        Member member = findMember();
        if(member instanceof CompetitiveSwimmer)
        {
            OutputHandler.printPleaseEnterTime();
            double input = InputHandler.inputDouble();
            if(input < ((CompetitiveSwimmer) member).getBrystsoevmningResultat() || ((CompetitiveSwimmer) member).getBrystsoevmningResultat() == 0)
            {
                ((CompetitiveSwimmer) member).setBrystsoevmningResultat(input);
                OutputHandler.printString("User Brystsvoemning time was changed to: " + ((CompetitiveSwimmer) member).getBrystsoevmningResultat());

            }
            else
            {
                OutputHandler.printErrorResultNotBetter();
            }
        }
        else
        {
            OutputHandler.printErrorUserType();
        }
        userData.outputToMemberDatabase();
    }
    public void updateTrainingTimeRygCrawl()
    {
        Member member = findMember();
        if(member instanceof CompetitiveSwimmer)
        {
            OutputHandler.printPleaseEnterTime();
            double input = InputHandler.inputDouble();
            if(input < ((CompetitiveSwimmer) member).getRygCrawlResultat() || ((CompetitiveSwimmer) member).getRygCrawlResultat() == 0)
            {
                ((CompetitiveSwimmer) member).setRygCrawlResultat(input);
                OutputHandler.printString("User RygCrawl time was changed to: " + ((CompetitiveSwimmer) member).getRygCrawlResultat());

            }
            else
            {
                OutputHandler.printErrorResultNotBetter();
            }
        }
        else
        {
            OutputHandler.printErrorUserType();
        }
        userData.outputToMemberDatabase();
    }

    public void printBestTournamentTimeForSpecificUser()
    {
        Member member = findMember();
        if(member != null)
        {
            OutputHandler.printString("Finding results and printing...");
            for(StaevneResultat staevneResultat: staevneResultatList)
            {
                if(staevneResultat.getCpr().equals(member.getCpr()))
                {
                    OutputHandler.printString(staevneResultat.toString());
                }
            }
        }
    }

    public void removeMember()
    {
        Member member = findMember();
        if(member != null)
        {
            memberList.remove(member);
            OutputHandler.printTextBoxEnd();
            OutputHandler.printRemovedMember();
            userData.outputToMemberDatabase();
        }
    }

    public void printResidualMembers()
    {
        OutputHandler.printMembersWhoHaveDebtMessage();
        for(Member member : memberList)
        {
            if(member.getDebt() > 0)
            {
                OutputHandler.printString(member.residualsString());
            }
        }
    }

    public void addToMemberList(Member member)
    {
        memberList.add(member);
        System.out.println(member.toString());
        userData.outputToMemberDatabase();
    }
}
