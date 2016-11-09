package com.seanyuan.virtualhumidor3.fragment;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

public class MyHumidorFragment extends BaseCigarListFragment {

    public MyHumidorFragment() {}

    @Override
    public Query getQuery(DatabaseReference databaseReference) {
        // All my posts
        return databaseReference.child("user-cigars")
                .child(getUid());
    }
}
