
package rest.o.gram.filters;

import rest.o.gram.entities.RestogramPhoto;
import rest.o.gram.utils.FilteringUtils;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: Itay
 * Date: 21/04/13
 */
public class HashtagRestogramFilter implements RestogramFilter {

    // TODO - initialize tags only once
    private final String foodTagsStr = "food, foodporn, foodie, foodart, instafood, insta_food, sharefood, share_food, " +
                                       "yum, yumi, yumi!, yummy, yami, munchies, getinmybelly, yumyum, hungry, delicious, " +
                                       "eat, dinner, diner, breakfast, lunch, sharefood, sweet, tagsta_food , dessert, stuffed, " +
                                       "eating, foodgasm, foodpic, foodpics, chef, cheff, foodstagram, brunch, bacon, eggs, " +
                                       "chocolate, pasta, icecream, pie, sandwich, salad, culinaryart, culinary_art, culinar, " +
                                       "culinary, yummi, restaurant, tasty, finedinning, foodforfoodies, foodstyling, israelfood, " +
                                       "israel_food, israelifood, israeli_food, gargeran, seafood, אוכל, מטבח, מסעדה, מסעדות, טעים, ארוחת, ארוחה, שף, בישול, גורמה, בשר, סושי";

    private final List<String> foodTagsList = Arrays.asList(foodTagsStr.split("\\s*,\\s*"));
    private final Set<String> foodTags = new HashSet<String>(foodTagsList);

    public void doFilter(final List<RestogramPhoto> data) {

        if (data == null)
            return;

        for (RestogramPhoto currPhoto : data)
        {
            final List<String> tags = Arrays.asList(currPhoto.getTags());
            final HashSet<String> tagsSet = new HashSet<String>(tags);

            if(!FilteringUtils.disjointSets(tagsSet, foodTags))
                currPhoto.setApproved(true);
        }
    }
}
