package olizarovich.probation.rest.test.services;

import olizarovich.probation.rest.models.Document;
import olizarovich.probation.rest.models.Person;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DataInit {
    private static String[] nameList = new String[] {"Paden", "Wilma", "Viona", "Dyan",
        "Luana", "Braidy", "Mavis", "Roxana", "Kaison", "Nicky"};

    /**
     * Creates persons entities.
     *
     * @param count Number of persons to generate
     * @return List of persons
     */
    public static List<Person> createPersonsData(int count) {
        List<Person> persons = new ArrayList<>();
        Random random = new Random();

        for (int i = 0; i<count; i++) {
            String name = nameList[random.nextInt(nameList.length)];
            String lastname = nameList[random.nextInt(nameList.length)];
            LocalDate localDate = LocalDate.of(random.nextInt(40) + 1980, random.nextInt(10) + 1, random.nextInt(20) + 1);
            persons.add(new Person(name, lastname, localDate));
        }

        return persons;
    }

    /**
     * Creates documents entities. Each person with each person in list (include himself).
     * Total number of document x^2, where x - number of persons
     *
     * @param persons List of persons for foreign Key
     * @return List of persons
     */
    public static List<Document> createDocumentsData(List<Person> persons) {
        ArrayList<Document> documents = new ArrayList<>();
        String[] documentsStatus = new String[]{"Ready", "In progress", "Unknown"};

        for (Person i : persons) {
            int statusNumber = 0;
            for (Person j : persons) {
                Document document = new Document();
                document.setTitle(i.getFirstName() + "Document");
                document.setStatus(documentsStatus[statusNumber % 3]);
                document.setCreationDate(LocalDate.of(2000, 1, 1).plusMonths(statusNumber));
                document.setExecutionPeriod(LocalDate.of(2000, 2, 1).plusMonths(statusNumber));
                document.setCustomer(i);
                document.setExecutor(j);

                documents.add(document);
                statusNumber++;
            }
        }

        return documents;
    }
}
