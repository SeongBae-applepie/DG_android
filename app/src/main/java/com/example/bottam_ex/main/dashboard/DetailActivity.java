package com.example.bottam_ex.main.dashboard;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.bottam_ex.R;
import com.example.bottam_ex.main.dashboard.frgmnet.DiseaseDetailFragment;
import com.example.bottam_ex.main.dashboard.frgmnet.EnemyInsectDetailFragment;
import com.example.bottam_ex.main.dashboard.frgmnet.PestInsectDetailFragment;
import com.example.bottam_ex.main.dashboard.frgmnet.OtherInsectDetailFragment;
import com.example.bottam_ex.main.dashboard.frgmnet.PathogenDetailFragment;
import com.example.bottam_ex.main.dashboard.frgmnet.WeedDetailFragment;

public class DetailActivity extends AppCompatActivity {

    private TextView detailTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        String detailUrl = getIntent().getStringExtra("detailUrl");

        Log.d("Detail_a", detailUrl);

        if (detailUrl == null) return;

        Fragment fragment = null;


        if (detailUrl.contains("SVC05")) {
            fragment = DiseaseDetailFragment.newInstance(detailUrl);//병

        } else if (detailUrl.contains("SVC06")) {
            fragment = PathogenDetailFragment.newInstance(detailUrl);//병원체

        } else if (detailUrl.contains("SVC07")) {
            fragment = PestInsectDetailFragment.newInstance(detailUrl);//해충

        } else if (detailUrl.contains("SVC08")) {
            fragment = OtherInsectDetailFragment.newInstance(detailUrl);//곤충

        } else if (detailUrl.contains("SVC15")) {
            fragment = EnemyInsectDetailFragment.newInstance(detailUrl);//천적곤충

        } else if (detailUrl.contains("SVC10")) {
            fragment = WeedDetailFragment.newInstance(detailUrl);

        }

        if (fragment != null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.detailContainer, fragment)
                    .commit();
        }
    }
}
