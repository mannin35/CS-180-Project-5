import java.util.*;

/**
 * Project 5 - src.Store
 * <p>
 * Encapsulates all the information a store has so that it
 * can be manipulated during runtime and written back to files.
 *
 * @author Rei Manning, Lab Sec L15
 * @version December 12, 2022
 */
public class Store {
    private String seller;
    private String name;

    //Constructor for new store, no stats yet
    public Store(String seller, String name) {
        this.seller = seller;
        this.name = name;
    }

    public String getSeller() {
        return this.seller;
    }

    public String getName() {
        return this.name;
    }

    @Override
    public String toString() {
        return seller + '\n' +
                name;
    }
}
