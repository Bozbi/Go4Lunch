package com.sbizzera.go4lunch.views.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.sbizzera.go4lunch.R;
import com.sbizzera.go4lunch.model.FakeWorkmates;
import com.sbizzera.go4lunch.views.adapters.RestaurantDetailWorkmateAdapter;

import java.util.List;

public class RestaurantDetailActivity extends AppCompatActivity {

    private List<FakeWorkmates> fakeWorkmatesList = FakeWorkmates.getWorkMatesList();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_detail);

        RestaurantDetailWorkmateAdapter adapter = new RestaurantDetailWorkmateAdapter(fakeWorkmatesList);
        RecyclerView rcv = findViewById(R.id.rcv);
        rcv.setLayoutManager(new LinearLayoutManager(this));
        rcv.setAdapter(adapter);

    }
}
