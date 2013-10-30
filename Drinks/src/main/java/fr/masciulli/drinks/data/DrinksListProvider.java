package fr.masciulli.drinks.data;


import java.util.ArrayList;
import java.util.List;

import fr.masciulli.drinks.model.DrinksListItem;

public class DrinksListProvider {
    public static List<DrinksListItem> getDrinks() {
        ArrayList<DrinksListItem> drinks = new ArrayList<DrinksListItem>();

        DrinksListItem amarettoFrost = new DrinksListItem();
        amarettoFrost.setName("Amaretto Frost");
        amarettoFrost.setImageURL("http://www.smallscreennetwork.com/videos/cocktail_spirit/morgenthaler-method-amaretto-sour.jpg");

        DrinksListItem americano = new DrinksListItem();
        americano.setName("Americano");
        americano.setImageURL("http://www.ganzomag.com/wp-content/uploads/2012/05/americano-cocktail1.jpg");

        DrinksListItem tomCollins = new DrinksListItem();
        tomCollins.setName("Tom Collins");
        tomCollins.setImageURL("http://www.vinumimporting.com/wp-content/uploads/2012/06/tom-collins.jpg");

        DrinksListItem mojito = new DrinksListItem();
        mojito.setName("Mojito");
        mojito.setImageURL("http://2eat2drink.files.wordpress.com/2011/04/mojito-final2.jpg");

        DrinksListItem dryMartini = new DrinksListItem();
        dryMartini.setName("Dry Martini");
        dryMartini.setImageURL("http://www.cocktailrendezvous.com/images.php?f=files/recipes/images/martini.jpg&w=616&h=347&c=1");

        drinks.add(amarettoFrost);
        drinks.add(americano);
        drinks.add(tomCollins);
        drinks.add(mojito);
        drinks.add(dryMartini);

        return drinks;
    }
}