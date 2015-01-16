package com.minecreatr.sinkingship.stats;

/**
 * Object Representation of the MySQL data
 *
 * @author minecreatr
 */
public class MySqlData {

    /**
     * Whether MySQL is even enabled
     */
    public static boolean isEnabled;

    /**
     * The Host
     */
    private String host;
    /**
     * The Port
     */
    private String port;
    /**
     * The User
     */
    private String user;
    /**
     * The Database password
     */
    private String password;

    public MySqlData(String host, String port, String user, String password){
        this.host=host;
        this.port=port;
        this.user=user;
        this.password=password;
    }
}
