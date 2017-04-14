package uk.co.giovannilenguito.controller;

import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;
import uk.co.giovannilenguito.helper.DatabaseHelper;
import uk.co.giovannilenguito.model.User;

import java.lang.reflect.Method;

/**
 * Created by giovannilenguito on 04/04/2017.
 */
public class UserControllerTest {
    @Test
    public void authenticate() throws Exception {
        DatabaseHelper databaseHelper = new DatabaseHelper();
        String username = "giovanni16.gl@gmail.com";
        String password = "cbf505a908804014f4b8ef7f62dd97e8"; //MD5 hashed password

        //Get user
        final User user = databaseHelper.getUser(username, null, null);


        UserController userController = new UserController();
        Method buildToken = userController.getClass().getDeclaredMethod("buildToken", JSONObject.class);
        if(!buildToken.isAccessible()){
            buildToken.setAccessible(true);
        }

        JSONObject credentials = new JSONObject();
        credentials.put("username", username);
        credentials.put("password", password);

        String token = (String) buildToken.invoke(userController, credentials);
        String expectedToken = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJnaW92YW5uaTE2LmdsQGdtYWlsLmNvbSJ9.t_iG9osGh3QdGLqS5DXvRBwC_clO9Q3lqTfjWoyx3G1RPBhVx9GlNGOgmMaM-8OwvR6-XV-g-_4IbWWZ2nW7qg";


        Assert.assertEquals(expectedToken, token);
        Assert.assertNotNull(user);
        Assert.assertEquals(password, user.getPassword());
    }

    @Test
    public void getUser() throws Exception {
        DatabaseHelper databaseHelper = new DatabaseHelper();
        User foundUser = databaseHelper.getUser(null, null, "d55e9a24f71d47729765fc7ccc631b71");
        databaseHelper.closeConnection();

        Assert.assertNotNull(foundUser);
    }

    @Test
    public void checkEmail() throws Exception {
        DatabaseHelper databaseHelper = new DatabaseHelper();

        final boolean result = databaseHelper.doesEmailExist("giovanni16.gl@gmail.com");
        databaseHelper.closeConnection();

        Assert.assertTrue(result);
    }

}