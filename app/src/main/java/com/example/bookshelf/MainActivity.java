package com.example.bookshelf;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        NavController navController = ((NavHostFragment)getSupportFragmentManager().findFragmentById(R.id.fragmentContainerView)).getNavController();
        NavigationUI.setupWithNavController(
                (Toolbar)findViewById(R.id.toolbar), navController,new AppBarConfiguration.Builder(navController.getGraph()).build());
    }
}