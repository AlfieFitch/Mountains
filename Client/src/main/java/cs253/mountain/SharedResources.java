package cs253.mountain;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


public class SharedResources {

    //URL of server
    public static final String BASE_URI = "http://localhost:8080/";

    //Result strings for comparison
    static final String EMPTY_RESULT = ""; //We make this explicit to be clear it should be empty not e.g. null
    static final String GET_ALL_INITIAL = """
            YrWyddfa is in the Eryri range in Cymru. It is in the Northern hemisphere and is 1085m high.
            Snowdon is in the Snowdonia range in Wales. It is in the Northern hemisphere and is 1085m high.
            Aconcagua is in the Andes range in Argentina. It is in the Southern hemisphere and is 6961m high.
            Annapurna is in the Himalayas range in Nepal. It is in the Northern hemisphere and is 8091m high.
            Makalu is in the Himalayas range in Nepal. It is in the Northern hemisphere and is 8485m high.
            Huascarán is in the Andes range in Peru. It is in the Southern hemisphere and is 6768m high.
            Antofalla is in the Andes range in Argentina. It is in the Southern hemisphere and is 6409m high.
            """;
    static final String ARGENTINA_INITIAL = """
            Aconcagua is in the Andes range in Argentina. It is in the Southern hemisphere and is 6961m high.
            Antofalla is in the Andes range in Argentina. It is in the Southern hemisphere and is 6409m high.
            """;
    static final String NEPAL_INITIAL = """
            Annapurna is in the Himalayas range in Nepal. It is in the Northern hemisphere and is 8091m high.
            Makalu is in the Himalayas range in Nepal. It is in the Northern hemisphere and is 8485m high.
            """;
    static final String NORTH_INITIAL = """
            YrWyddfa is in the Eryri range in Cymru. It is in the Northern hemisphere and is 1085m high.
            Snowdon is in the Snowdonia range in Wales. It is in the Northern hemisphere and is 1085m high.
            Annapurna is in the Himalayas range in Nepal. It is in the Northern hemisphere and is 8091m high.
            Makalu is in the Himalayas range in Nepal. It is in the Northern hemisphere and is 8485m high.
            """;
    static final String SOUTH_INITIAL = """
            Aconcagua is in the Andes range in Argentina. It is in the Southern hemisphere and is 6961m high.
            Huascarán is in the Andes range in Peru. It is in the Southern hemisphere and is 6768m high.
            Antofalla is in the Andes range in Argentina. It is in the Southern hemisphere and is 6409m high.
            """;
    static final String NEPAL_OVER_8400 = """
            Makalu is in the Himalayas range in Nepal. It is in the Northern hemisphere and is 8485m high.
            """;
    static final String GET_YR_WYDDFA = """
            YrWyddfa is in the Eryri range in Cymru. It is in the Northern hemisphere and is 1085m high.
            """;
    static final String WALES_AFTER_ADDING = """
            Snowdon is in the Snowdonia range in Wales. It is in the Northern hemisphere and is 1085m high.
            PenYFan is in the BannauBrycheiniog range in Wales. It is in the Northern hemisphere and is 886m high.
            CadairIdris is in the Eryri range in Wales. It is in the Northern hemisphere and is 893m high.
            """;

    static final String UPDATED_IN_NEPAL = """
            Annapurna is in the Annapurna range in Nepal. It is in the Northern hemisphere and is 8091m high.
            Makalu is in the Himalayas range in Nepal. It is in the Northern hemisphere and is 8485m high.
            """;

    static final String UPDATED_IN_ARGENTINA = """
            Aconcagua is in the Andes range in Argentina. It is in the Southern hemisphere and is 6961m high.
            """;

    static final String GET_BY_ID = """
            Annapurna is in the Himalayas range in Nepal. It is in the Northern hemisphere and is 8091m high.
            """;

    /*
    You can use this for debugging - it will output the path of the request, the status code of the response,
    the content type of the response, and the request's query parameters. You can edit it to access other
    parts of the response or the request. Call it e.g. like this:
    SharedResources.outputResult(connector.getByCountry("Nepal"));
     */
    static void outputResult(final Optional<Response> response) {
        System.out.println("Result Returned:");
        response.ifPresentOrElse(item -> {
            for (Mountain mountain : item.getMountains()) {
                System.out.println(mountain);
            }
            System.out.println("Request path: " + item.getResponse().request().uri().getPath()
                    + ", Response status code: " + item.getResponse().statusCode()
                    + ", Response content type: " + item.getResponse().headers().firstValue("content-type")
                    + ", Request query parameters: " + item.getResponse().request().uri().getQuery());
            //If it prints the message below, then the result is an optional with empty content.
        }, () -> System.out.println("Error Response (empty optional)"));
    }

    /*
    Used to check that the result (and *only* the result - not e.g. the return code, content type or parameters) is
    correct. Checks to see if the appropriate parameter passing methods are used; the appropriate return codes are
    used; and the returned data types are best practice are in the hidden tests.
     */
    static boolean checkResult(final String expectedOutput, final Optional<Response> response) {
        //System.out.println(response);
        if (response.isPresent()) {
            String resStr = arrayListToString(response.get().getMountains());
            boolean result = expectedOutput.equals(resStr);
            //Can be handy for debugging:
            if (!result) {
                System.out.println("Expected:");
                System.out.println(expectedOutput);
                outputResult(response);
            }
            return result;
        } else {
            return false;
        }
    }

    /*
    Populate with the appropriate test data
     */
    static ArrayList<Mountain> addTestData() {
        Mountain yrWyddfa = new Mountain("YrWyddfa", 1085,
                "Eryri", "Cymru", true);
        Mountain snowden = new Mountain("Snowdon", 1085,
                "Snowdonia", "Wales", true);
        Mountain aconcagua = new Mountain("Aconcagua", 6961, "Andes",
                "Argentina", false);
        Mountain annapurna = new Mountain("Annapurna", 8091, "Himalayas",
                "Nepal", true);
        Mountain makalu = new Mountain("Makalu", 8485, "Himalayas", "Nepal",
                true);
        Mountain huascaran = new Mountain("Huascarán", 6768, "Andes", "Peru",
                false);
        Mountain antofalla = new Mountain("Antofalla", 6409, "Andes", "Argentina",
                false);
        ArrayList<Mountain> addList = new ArrayList<>();
        addList.add(yrWyddfa);
        addList.add(snowden);
        addList.add(aconcagua);
        addList.add(annapurna);
        addList.add(makalu);
        addList.add(huascaran);
        addList.add(antofalla);
        return addList;
    }

    /*
    Convert a list of mountains to a string - will crash if the list is null (so always ensure you return an
    empty list when you don't want to return data).
     */
    private static String arrayListToString(List<Mountain> mountainList) {
        StringBuffer buffer = new StringBuffer();
        for (Mountain mountain : mountainList) {
            buffer.append(mountain).append("\n");
        }
        return buffer.toString();
    }
}
