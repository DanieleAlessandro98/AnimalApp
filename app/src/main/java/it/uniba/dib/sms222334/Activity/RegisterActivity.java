package it.uniba.dib.sms222334.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.IntRange;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import it.uniba.dib.sms222334.R;
import it.uniba.dib.sms222334.Utils.UserRole;
import it.uniba.dib.sms222334.Views.Adapter.AnimalAppPageAdapter;

public class RegisterActivity extends AppCompatActivity {
    final static String TAG="RegisterActivity";
    private TabLayoutMediator tabLayoutMediator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_layout);


        ViewPager2 pager =findViewById(R.id.register_viewpager);
        pager.setAdapter(new AnimalAppPageAdapter(this));

        TabLayout tabLayout = (TabLayout) findViewById(R.id.dotted_tab_layout);
        tabLayoutMediator= new TabLayoutMediator(tabLayout,pager, (tab, position) -> {

        });
        tabLayoutMediator.attach();

        tabLayout.selectTab(tabLayout.getTabAt(1));

    }

    public void registerSuccesfull(UserRole Role){
        final Intent authIntent =new Intent();
        authIntent.putExtra("user-role",Role);
        setResult(RESULT_OK,authIntent);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        tabLayoutMediator.detach();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_down_in, R.anim.slide_down_out);
        setResult(RESULT_CANCELED);
        finish();
    }
}
