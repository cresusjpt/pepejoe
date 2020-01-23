package com.saltechdigital.pizzeria;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.room.Room;
import androidx.test.InstrumentationRegistry;
import androidx.test.runner.AndroidJUnit4;

import com.saltechdigital.pizzeria.database.PizzeriaDatabase;
import com.saltechdigital.pizzeria.models.Categorie;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class CategorieDaoTest {
    private PizzeriaDatabase database;

    // DATA SET FOR TEST
    private static int USER_ID = 2;
    private static Categorie USER_DEMO = new Categorie(USER_ID, "Philippe", "https://www.google.fr, ");

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Before
    public void initDb() throws Exception {
        this.database = Room.inMemoryDatabaseBuilder(InstrumentationRegistry.getContext(),
                PizzeriaDatabase.class)
                .allowMainThreadQueries()
                .build();
    }

    @Test
    public void insertAndGetUser() throws InterruptedException {
        // BEFORE : Adding a new user
        this.database.categorieDao().insertCategorie(USER_DEMO);
        // TEST
        Categorie user = LiveDataTestUtil.getValue(this.database.categorieDao().getCategorie(USER_ID));
        assertTrue(user.getDescription().equals(USER_DEMO.getDescription()) && user.getIdCategorie() == USER_ID);
    }

    @After
    public void closeDb() throws Exception {
        database.close();
    }
}
