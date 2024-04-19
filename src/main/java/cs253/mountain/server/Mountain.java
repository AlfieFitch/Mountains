package cs253.mountain.server;

import java.util.Objects;

/**
 * Class that represents a mountain - mountains have an integer ID, a name, an altitude (height), a range (mountain
 * range), and a country; they are also either in the Northern hemisphere or not.
 *
 * Mountains need to be represented on both the service and the client - and you may or may not want to use the code
 * below as the basis for the version on the service. But you probably should use this code for the client.
 *
 * Note that the integer ID should be uniquely generated on the service when a mountain is created - how you do this
 * is up to you provided it's unique and an integer. It's fine if the ID changes when you update a mountain.
 *
 * This version of Mountain just generates a dummy ID of 0 because it's intended for you to use in your client code - as
 * a simplification, there is no need to worry about this: it's simpler if the Mountain objects that are created on the
 * client to upload to the server (either as new or replacement data) than to deal wih the fact that the ones you upload
 * are different to the ones you download because the uploaded ones don't need an ID.
 *
 * NOTE: You will HAVE to change this code for the server to automatically generate unique IDs and you may choose to
 * change it (or add to it) in other ways too.
 */
public final class Mountain {

    private static final String FORMAT_STRING = "%s is in the %s range in %s. It is in the "
            + "%s hemisphere and is %dm high.";
    private static final String NORTH = "Northern";
    private static final String SOUTH = "Southern";
    private int id;
    private String name;
    private int altitude;
    private String range;
    private String country;
    private boolean isNorthern;

    /**
     * Create an empty Mountain object
     */
    public Mountain(){}

    /**
     * Create a Mountain object populated with data an an ID of zero
     * @param id the Mountain id
     * @param name the Mountain name
     * @param altitude the Mountain's altitude (height) in m
     * @param range the Mountain's mountain range
     * @param country the Mountain's country
     * @param isNorthern true if in the Northern hemisphere, false otherwise
     */
    public Mountain(final int id, final String name, final int altitude, final String range, final String country,
                    final boolean isNorthern) {
        this.id = id;
        this.setName(name);
        this.setAltitude(altitude);
        this.setRange(range);
        this.setCountry(country);
        this.setNorthern(isNorthern);
    }

    /**
     * Return the Mountain's ID - should be zero (although there is a setID method so it could in principle change
     * @return the Mountain's ID
     */
    public int getId() {
        return id;
    }

    /**
     * Return the Mountain's name
     * @return the Mountain's name
     */
    public String getName() {
        return name;
    }

    /**
     * Return the Mountain's altitude (height)
     * @return the Mountain's altitude
     */
    public int getAltitude() {
        return altitude;
    }

    /**
     * Return the Mountain's range
     * @return the Mountain's mountain range
     */
    public String getRange() {
        return range;
    }

    /**
     * Return the Mountain's country
      * @return the Mountain's country
     */
    public String getCountry() {
        return country;
    }

    /**
     * The Mountain's hemisphere
     * @return true if it is in the Northern hemisphere, false otherwise
     */
    public boolean getIsNorthern() {
        return isNorthern;
    }

    /**
     * Set the Mountain's ID - note this may be needed when Mountain data is downloaded in JSON (or whatever)
     * format from the server and turned into Mountain objects on the client
     * @param id the Mountain's new ID
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Set the Mountain's name
     * @param name the Mountain's new name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Set the Mountain's altitude
     * @param altitude the Mountain's new altitude
     */
    public void setAltitude(int altitude) {
        this.altitude = altitude;
    }

    /**
     * Set the Mountain's range
     * @param range the Mountain's new mountain range
     */
    public void setRange(String range) {
        this.range = range;
    }

    /**
     * Set the Mountain's country
     * @param country the Mountain's new country
     */
    public void setCountry(String country) {
        this.country = country;
    }

    /**
     * Set the Mountain's new hemisphere
     * @param northern true if it's in the Northern hemisphere, false otherwise
     */
    public void setNorthern(boolean northern) {
        isNorthern = northern;
    }

    /**
     * Generate a readable string representation of a Mountain. Note this is uesd to check that your code is correct,
     * and it does not include the ID because how you choose to generate that is up to you
     * @return the Mountain as a readable String
     */
    @Override
    public String toString() {
        return String.format(FORMAT_STRING, name, range, country, isNorthern ? NORTH : SOUTH, altitude);
    }

    /**
     * Checks if an arbitrary object is equal to this Mountain - used by a variety of API methods (e.g. contains in
     * List). Note that equality for our Mountain objects means the same name, range and country - we ignore altitude
     * and hemisphere
     * @param obj the object being compared to
     * @return true if this object is equal, false otherwise
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Mountain mountain)) {
            return false;
        }
        return name.equals(mountain.getName()) && range.equals(mountain.getRange())
                && country.equals(mountain.getCountry());
    }

    /**
     * It is essential if you implement equals you also implement hashCode and calculate the hash from the same things
     * you use to check equality - failing to do this means that two objects which are equal don't have the same hashCode
     * and vice versa, which breaks some APIs (e.g. hash maps)
     * @return
     */
    @Override
    public int hashCode() {
        return Objects.hash(name, range, country);
    }
}

