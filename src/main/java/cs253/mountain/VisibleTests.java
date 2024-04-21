package cs253.mountain;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/*
Perform a range of calls to test the service:
1. Loads the initial data set
2. Checks the methods to get/read data work
3. Adds additional data and checks it's accessible - checks that adding it again does not duplicate it
4. Updates data - looks up a mountain by ID, updates it and checks it's updated
5. Deletes data -
 */
public class VisibleTests {
        public static void main(String[] args) {
        MountainConnector connector = new MountainConnector(SharedResources.BASE_URI);

        //LOAD INITIAL DATA
        System.out.println("**Level 1**");
        System.out.print("\nLoad Initial Data: ");
        System.out.println(SharedResources.checkResult("",
                connector.addMountains( SharedResources.addTestData())) ? "success" : "failure");

        //GET TESTS
        System.out.println("**Level 2**");
        System.out.print("Get all Mountains: "); 
        System.out.println(SharedResources.checkResult(SharedResources.GET_ALL_INITIAL,
                connector.getAll()) ? "success" : "failure");
        System.out.print("Get mountains in Argentina: ");
        System.out.println(SharedResources.checkResult(SharedResources.ARGENTINA_INITIAL,
                connector.getByCountry("Argentina")) ? "success" : "failure");
        System.out.print("Get mountains in Nepal and the Himalayas: ");
        System.out.println(SharedResources.checkResult(SharedResources.NEPAL_INITIAL,
                connector.getByCountryAndRange("Nepal", "Himalayas")) ? "success" : "failure");
        System.out.print("Get mountains in the Northern hemisphere: ");
        System.out.println(SharedResources.checkResult(SharedResources.NORTH_INITIAL,
                connector.getByHemisphere(true)) ? "success" : "failure");
        System.out.print("Get mountains in the Southern hemisphere: ");
        System.out.println(SharedResources.checkResult(SharedResources.SOUTH_INITIAL,
                connector.getByHemisphere(false)) ? "success" : "failure");
        System.out.print("Get mountains over 8400m in Nepal: ");
        System.out.println(SharedResources.checkResult(SharedResources.NEPAL_OVER_8400,
                connector.getByCountryAltitude("Nepal", 8400)) ? "success" : "failure");
        System.out.print("Get a specific mountain: ");
        System.out.println(SharedResources.checkResult(SharedResources.GET_YR_WYDDFA,
                connector.getByName("Cymru", "Eryri", "YrWyddfa")) ? "success" : "failure");
        System.out.print("Simple error test: ");
        System.out.println(SharedResources.checkResult(SharedResources.EMPTY_RESULT,
                connector.getByCountry("lemon")) ? "success" : "failure");

        //ADD ADDITIONAL DATA
        System.out.println("**Level 3**");
        //Create a list of two new mountains
        Mountain penYFan = new Mountain("PenYFan", 886, "BannauBrycheiniog", "Wales", true);
        Mountain cadairIdris = new Mountain("CadairIdris", 893, "Eryri", "Wales", true);
        List<Mountain> addList = new ArrayList<>();
        addList.add(penYFan);
        addList.add(cadairIdris);

        //Now try adding these to the list
        System.out.print("Adding new mountains: ");
        System.out.println(SharedResources.checkResult(SharedResources.EMPTY_RESULT,
                connector.addMountains(addList)) ? "success" : "failure");
        System.out.print("Get all mountains (new added) in Wales: ");
        System.out.println(SharedResources.checkResult(SharedResources.WALES_AFTER_ADDING,
                connector.getByCountry("Wales"))? "success" : "failure");

        /*Now do it again - the response code (not checked here - in the hidden tests remember) should indicate that
        this has failed, but the result of adding the same data again should not change the list of data stored*/
        System.out.print("Try adding again: ");
        System.out.println(SharedResources.checkResult(SharedResources.EMPTY_RESULT,
                connector.addMountains(addList)) ? "success" : "failure");
        System.out.print("Get all mountains again (should be unchanged): ");
        System.out.println(SharedResources.checkResult(SharedResources.WALES_AFTER_ADDING,
                connector.getByCountry("Wales"))? "success" : "failure");

        //UPDATE
        System.out.println("**Level 4**");
        /*Annapurna is more usually reported as being in the Annapurna range, not the Himalayas
         */
        //First create the new mountain data
        Mountain updateAnnapurna = new Mountain("Annapurna", 8091, "Annapurna", "Nepal", true);

        /*We need to know the ID of the mountain we are replacing - so we need to look it up by name first
        because we won't know its ID for certain - that's implementation dependent*/
        Optional<Response> annapurna = connector.getByName("Nepal", "Himalayas", "Annapurna");
        int id = annapurna.get().getMountains().get(0).getId();//** see below
        //Ensure checkById() method works
        System.out.print("Check get by ID: ");
        System.out.println(SharedResources.checkResult(SharedResources.GET_BY_ID,
                connector.getById(id)) ? "success" : "failure");
        System.out.print("Update mountain: ");
        //System.out.println(SharedResources.checkResult(SharedResources.EMPTY_RESULT,
         //       connector.updateMountain(id, updateAnnapurna)) ? "success" : "failure");
        System.out.print("Check mountains in Nepal updated: ");
        System.out.println(SharedResources.checkResult(SharedResources.UPDATED_IN_NEPAL,
                connector.getByCountry("Nepal"))? "success" : "failure");

        //DELETE
        System.out.println("**Level 5**");
        //Delete Antofalla

        /*We need to know the ID of the mountain we are deleting - so we need to look it up by name first
        because we won't know its ID for certain - that's implementation dependent*/
        Optional<Response> getAntofalla = connector.getByName("Argentina", "Andes", "Antofalla");
        id = getAntofalla.get().getMountains().get(0).getId();//** see similar code and comment in UpdateData
        System.out.print("Delete mountain: ");
        //System.out.println(SharedResources.checkResult(SharedResources.EMPTY_RESULT,
         //       connector.deleteMountain(id)) ? "success" : "failure");
        System.out.print("Check mountains in Argentina updated: ");
        System.out.println(SharedResources.checkResult(SharedResources.UPDATED_IN_ARGENTINA,
                connector.getByCountry("Argentina"))? "success" : "failure");
        }
}