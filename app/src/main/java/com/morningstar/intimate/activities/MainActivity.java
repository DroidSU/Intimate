/*
 * Created by Sujoy Datta. Copyright (c) 2019. All rights reserved.
 *
 * To the person who is reading this..
 * When you finally understand how this works, please do explain it to me too at sujoydatta26@gmail.com
 * P.S.: In case you are planning to use this without mentioning me, you will be met with mean judgemental looks and sarcastic comments.
 */

package com.morningstar.intimate.activities;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.bvapp.arcmenulibrary.ArcMenu;
import com.bvapp.arcmenulibrary.widget.FloatingActionButton;
import com.morningstar.intimate.R;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    private static final String[] MENU_ITEMS_NAMES = {"Import Photo", "Import Video"};
    private static final int[] MENU_ITEMS_COLORS = {R.color.colorPrimary, R.color.secondaryDarkColor};
    private static final int[] ITEM_DRAWABLES = {R.drawable.ic_add_photos_black_24dp, R.drawable.ic_video_black_24dp};
    @BindView(R.id.mainActivityToolbar)
    Toolbar toolbar;
    @BindView(R.id.arcMenu)
    ArcMenu arcMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        initialiseArcMenu();
        initArcMenu(arcMenu);
    }

    private void initialiseArcMenu() {
        arcMenu.setToolTipTextSize(14);
        arcMenu.setMinRadius(60);
        arcMenu.setArc(175, 255);
        arcMenu.setToolTipSide(ArcMenu.TOOLTIP_UP);
        arcMenu.setToolTipTextColor(Color.WHITE);
        arcMenu.setToolTipBackColor(Color.parseColor("#88000000"));
        arcMenu.setToolTipCorner(4f);  //set tooltip corner
        arcMenu.setToolTipPadding(4f);  //set tooltip padding
        arcMenu.setColorNormal(getResources().getColor(R.color.md_green_700));
        arcMenu.showTooltip(true);
        arcMenu.setDuration(600);
    }

    private void initArcMenu(final ArcMenu menu) {
        for (int i = 0; i < MainActivity.ITEM_DRAWABLES.length; i++) {
            FloatingActionButton item = new FloatingActionButton(this);  //Use internal fab as a child
            item.setSize(FloatingActionButton.SIZE_MINI);  //set minimum size for fab 42dp
            item.setShadow(true); //enable to draw shadow
            item.setBackgroundColor(getResources().getColor(MENU_ITEMS_COLORS[i]));
            item.setIcon(MainActivity.ITEM_DRAWABLES[i]); //add icon for fab
            menu.setChildSize(item.getIntrinsicHeight()); // fit menu child size exactly same as fab

            final int position = i;
            menu.addItem(item, MainActivity.MENU_ITEMS_NAMES[i], new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(MainActivity.this, MainActivity.MENU_ITEMS_NAMES[position],
                            Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
