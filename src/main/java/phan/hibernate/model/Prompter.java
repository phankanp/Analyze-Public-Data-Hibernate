package phan.hibernate.model;

import java.io.IOException;
import java.util.*;

import static phan.hibernate.model.HibernateSessions.*;

public class Prompter {
    Scanner scanner = new Scanner(System.in);

    private Map<String, String> mMenu;

    public Prompter() {
        mMenu = new LinkedHashMap<String, String>();

        mMenu.put("1", "View All Data For Countries");
        mMenu.put("2", "Add Country.");
        mMenu.put("3", "Edit Country Data.");
        mMenu.put("4", "View Statistics");
        mMenu.put("5", "Delete Country");
        mMenu.put("6", "Exit");
    }

    private String promptAction() throws IOException {
        for (Map.Entry<String, String> option : mMenu.entrySet()) {
            System.out.printf("%s - %s %n", option.getKey(), option.getValue());
        }
        System.out.printf("%nWhat is your choice: %n");
        String choice = scanner.nextLine();
        return choice.trim().toLowerCase();
    }

    public void runPrompt() {
        String choice = " ";

        do try {
            choice = promptAction();
            switch (choice) {
                case "1":
                    allCountriesData(fetchAllCountries());
                    System.out.println();
                    break;
                case "2":
                    addCountry();
                    System.out.println();
                    break;
                case "3":
                    editCountry();
                    System.out.println();
                    break;
                case "4":
                    printStats();
                    System.out.println();
                    break;
                case "5":
                    deleteCountry(fetchAllCountries());
                    System.out.println();
                    break;
                case "6":
                    System.out.println("Exiting program");
                    break;
                default:
                    System.out.println("Problem with input, try again");
                    System.out.println();
                    break;
            }
        } catch (IOException ioe) {
            System.out.println("Problem with input");
            ioe.printStackTrace();
        } while (!choice.equals("6"));
    }

    private static void allCountriesData(List<Country> countries) {
        System.out.printf("%s %27s %20s %22s %n",
                "Country Code", "Country Name", "Internet Users", "Adult Literacy Rate");
        System.out.println("-------------------------------------------------------------------------------------");

        for (Country country : countries) {
            if (country.getInternetUsers() != null && country.getAdultLiteracyRate() != null) {
                System.out.printf("%6s %32s %17.2f %20.2f %n", country.getCode(), country.getName(), country
                                .getInternetUsers(),
                        country.getAdultLiteracyRate());
            } else if (country.getInternetUsers() == null && country.getAdultLiteracyRate() == null) {
                System.out.printf("%6s %32s %17s %20s %n", country.getCode(), country.getName(), "---", "---");
            } else if (country.getInternetUsers() == null && country.getAdultLiteracyRate() != null) {
                System.out.printf("%6s %32s %17s %20.2f %n", country.getCode(), country.getName(), "---",
                        country.getAdultLiteracyRate());
            } else if (country.getInternetUsers() != null && country.getAdultLiteracyRate() == null) {
                System.out.printf("%6s %32s %17.2f %20s %n", country.getCode(), country.getName(), country
                        .getInternetUsers(), "---");
            }
        }
    }

    private void addCountry() throws IOException {
        String countryCode = setUpdateCountryCode();
        String countryName = setUpdateCountryName();
        Double internetUsers = setUpdateInternetUsers();
        Double adultLiteracy = setUpdateAdultLiteracy();

        Country country = new Country.CountryBuilder()
                .withCode(countryCode)
                .withName(countryName)
                .withInternetUsers(internetUsers)
                .withAdultLiteracyRate(adultLiteracy)
                .build();

        save(country);
    }

    private void editCountry() throws IOException {
        allCountriesData(fetchAllCountries());
        System.out.println();

        Country countryChoice = getCountryCode();

        boolean isValid = true;
        do {
            System.out.println();
            System.out.println("1\t Internet Users");
            System.out.println("2\t Adult Literacy");
            System.out.println();

            System.out.print("Enter the number of the country data you would like to edit: ");
            int choice = scanner.nextInt();

            switch (choice) {
                case 1:
                    Double newInternetUsers = setUpdateInternetUsers();
                    countryChoice.setInternetUsers(newInternetUsers);
                    update(countryChoice);

                    System.out.printf("Internet users has be updated to %.2f", newInternetUsers);

                    break;
                case 2:
                    Double newAdultLiteracy = setUpdateAdultLiteracy();
                    countryChoice.setAdultLiteracyRate(newAdultLiteracy);
                    update(countryChoice);

                    System.out.printf("Adult Literacy rate has be updated to %.2f", newAdultLiteracy);

                    break;
                default:
                    System.out.println("Invalid selection choice, please enter a number from the list: ");
                    isValid = false;
                    break;
            }
        } while (!isValid);
    }

    private void printStats() {
        System.out.printf("%n%s has the least amount of internet users at %.2f percent", minInternetUsers().getName()
                , minInternetUsers().getInternetUsers());
        System.out.printf("%n%s has the most amount of internet users at %.2f percent", maxInternetUsers().getName(),
                maxInternetUsers().getInternetUsers());
        System.out.printf("%n%s has the least adult literacy rate at %.2f percent", minAdultLiteracy().getName(),
                minAdultLiteracy().getAdultLiteracyRate());
        System.out.printf("%n%s has the most adult literacy rate at %.2f percent", maxAdultLiteracy().getName(),
                maxAdultLiteracy().getAdultLiteracyRate());
        System.out.printf("%nCorrelation coefficient: %.2f%n", calculateCorrelationCoefficient());

    }

    private void deleteCountry(List<Country> countries) throws IOException {
        allCountriesData(countries);

        Country countryChoice = getCountryCode();

        delete(countryChoice);

        System.out.printf(countryChoice.getName() + " has been deleted%n");

    }

    private Country minInternetUsers() {
        return fetchAllCountries()
                .stream()
                .filter(country -> country.getInternetUsers() != null)
                .min(Comparator.comparing(Country::getInternetUsers))
                .get();
    }

    private Country maxInternetUsers() {
        return fetchAllCountries()
                .stream()
                .filter(country -> country.getInternetUsers() != null)
                .max(Comparator.comparing(Country::getInternetUsers))
                .get();
    }

    private Country minAdultLiteracy() {
        return fetchAllCountries()
                .stream()
                .filter(country -> country.getAdultLiteracyRate() != null)
                .min(Comparator.comparing(Country::getAdultLiteracyRate))
                .get();
    }

    private Country maxAdultLiteracy() {
        return fetchAllCountries()
                .stream()
                .filter(country -> country.getAdultLiteracyRate() != null)
                .max(Comparator.comparing(Country::getAdultLiteracyRate))
                .get();
    }

    private Double calculateCorrelationCoefficient() {
        Double count = 0.0;
        Double sumOfInternetUsers = 0.0;
        Double sumOfLiteracyRate = 0.0;
        Double multInternetLiteracy = 0.0;
        Double internetSquared = 0.0;
        Double literacySquared = 0.0;

        for (Country country : fetchAllCountries()) {
            if (country.getAdultLiteracyRate() != null && country.getInternetUsers() != null) {
                count++;
                sumOfInternetUsers = sumOfInternetUsers + country.getInternetUsers();
                sumOfLiteracyRate = sumOfLiteracyRate + country.getAdultLiteracyRate();
                multInternetLiteracy = multInternetLiteracy + (country.getInternetUsers() * country
                        .getAdultLiteracyRate());
                internetSquared = internetSquared + (Math.pow(country.getInternetUsers(), 2));
                literacySquared = literacySquared + (Math.pow(country.getAdultLiteracyRate(), 2));

            }
        }

        Double dividend = ((count * multInternetLiteracy) - (sumOfInternetUsers * sumOfLiteracyRate));

        Double left = (count * internetSquared) - (Math.pow(sumOfInternetUsers, 2));

        Double right = (count * literacySquared) - (Math.pow(sumOfLiteracyRate, 2));

        Double divisor = Math.sqrt(left * right);

        return (dividend / divisor);
    }

    private String setUpdateCountryCode() throws IOException {
        String countryCode;

        System.out.print("Enter a 3 digit country code: ");
        countryCode = scanner.next().toUpperCase();

        while (true) {
            if (countryCode.length() != 3) {
                System.out.print("Country code must be 3 digits, please try again: ");
                countryCode = scanner.next().toUpperCase();
            } else if (countryCode.length() == 3) {
                Country country = findByCode(countryCode);
                if (country != null) {
                    System.out.print("The entered country code already exists, please try again: ");
                    countryCode = scanner.next().toUpperCase();
                } else {
                    break;
                }
            }
        }

        return countryCode;
    }

    private Country getCountryCode() throws IOException {
        String countryCode;
        boolean isValid;

        Country countryChoice = new Country();

        do {
            System.out.print("Enter a 3 digit country code for the country you want to delete or edit: ");
            countryCode = scanner.nextLine().toUpperCase();

            if (countryCode.length() != 3) {
                System.out.println("Country code must be 3 digits, please try again: ");
                isValid = false;
            } else {
                countryChoice = findByCode(countryCode);
                if (countryChoice == null) {
                    isValid = false;
                } else {
                    isValid = true;
                }
            }
        } while (!isValid);

        return countryChoice;
    }

    private String setUpdateCountryName() throws IOException {
        String countryName;
        boolean isValid = false;

        System.out.print("Enter the country name: ");
        countryName = scanner.next();

        while (true){
            if (countryName.matches("[a-zA-Z._^%$#!~@,\\s]+")) {
                break;
            } else {
                System.out.print("Country name must only contain letters, please enter the country name: ");
                countryName = scanner.next();
            }
        }

        return countryName;
    }

    boolean ifDecimal(String input) {
        try {
            Double.parseDouble(input);
        } catch (NumberFormatException ex) {
            return false;
        }

        return true;
    }

    private Double getaDouble(Double internetUsers, boolean isValid, String s, String s2) {
        System.out.print(s);
        String internetUsersEmpty = scanner.next();
        scanner.nextLine();

        do {
            if (internetUsersEmpty.isEmpty()) {
                internetUsers = null;
                isValid = true;
            } else if (ifDecimal(internetUsersEmpty)) {
                internetUsers = Double.parseDouble(internetUsersEmpty);
                if (internetUsers == 0.0) {
                    internetUsers = null;
                    isValid = true;
                } else {
                    isValid = true;
                }
            } else {
                System.out.println(s2);
            }
        } while (!isValid);
        return internetUsers;
    }

    private Double setUpdateInternetUsers() throws IOException {
        Double internetUsers = null;
        boolean isValid = false;

        internetUsers = getaDouble(internetUsers, isValid, "Enter the percent of internet users (0 for null): ",
                "Internet Users percent must contain numbers only, please try again: ");

        return internetUsers;
    }

    private Double setUpdateAdultLiteracy() throws IOException {
        Double adultLiteracy = null;
        boolean isValid = false;

        adultLiteracy = getaDouble(adultLiteracy, isValid, "Enter the adult literacy percent (0 for null): ",
                "Adult literacy percent must contain numbers only, please try again");

        return adultLiteracy;
    }
}
