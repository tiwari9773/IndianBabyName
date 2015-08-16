package in.sel.indianbabyname;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.transition.Transition;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewAnimationUtils;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import in.sel.adapter.NameRecycleViewAdapter;
import in.sel.customview.CustomDividerItemDecoration;
import in.sel.dbhelper.DBHelper;
import in.sel.dbhelper.TableContract;
import in.sel.logging.AppLogger;
import in.sel.model.M_Name;
import in.sel.utility.AppConstants;
import xyz.danoz.recyclerviewfastscroller.vertical.VerticalRecyclerViewFastScroller;

/**
 * Class is designed for Developer For Marking of Name
 */
@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class ActivityDisplayName extends AppCompatActivity implements OnClickListener {
    private String TAG = getClass().getName();

    private RecyclerView lsName;
    private Toolbar toolbar;
    /** */
    public static String selectedAlphabet = "";

    /** */
    private DBHelper dbHelper;

    /** Parent View*/
    private static final long ANIM_DURATION = 1000;
    private View bgViewGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_name_list_recycle_view);

        setupLayout();
        setupWindowAnimations();

        init();
    }

    private void init() {

        /** Set Up Toolbar*/
        toolbar = (Toolbar) findViewById(R.id.tb_app_bar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        dbHelper = new DBHelper(this);

        String selectedAlphabet = getIntent().getStringExtra(ActivityAlphabetMain.SELECTED_ALPHA_BET);

        /** This is will select only those which are not marked */
        String where = TableContract.Name.NAME_EN + " like '" + selectedAlphabet + "%' ORDER BY " + TableContract.Name.NAME_FRE + " DESC";

        Cursor c = dbHelper.getTableValue(TableContract.Name.TABLE_NAME, new String[]{TableContract.Name.AUTO_ID,
                TableContract.Name.NAME_EN, TableContract.Name.NAME_MA, TableContract.Name.NAME_FRE,
                TableContract.Name.GENDER_CAST}, where);

        if (c != null && c.getCount() > 0) {

            if (AppConstants.DEBUG)
                AppLogger.ToastLong(this, c.getCount() + "");

            List<M_Name> name = parseListName(c);
            displayList(name);

			/* */
            TextView tvTotal = (TextView) findViewById(R.id.tvTotal);
            String s = String.format(getResources().getString(R.string.lable_total_cout), c.getCount());
            tvTotal.setText(s);

			/* Sorting on Name based on English Name */
            //TextView tvEnName = (TextView) findViewById(R.id.tvEnglish);
            // tvEnName.setOnClickListener(this);

			/* Sorting on Name based on Marathi Name */
            //TextView tvHinName = (TextView) findViewById(R.id.tvHindi);
            // tvHinName.setOnClickListener(this);

			/* Sorting on Name based on Frequency */
            //TextView tvFrequ = (TextView) findViewById(R.id.tvFrequency);
            // tvFrequ.setOnClickListener(this);
        } else {
            if (c != null)
                c.close();
        }


    }

    private void setupLayout() {
        bgViewGroup = findViewById(R.id.container);
    }

    private void setupWindowAnimations() {
        setupEnterAnimations();
        setupExitAnimations();
    }

    private void setupEnterAnimations() {
        Transition enterTransition = getWindow().getSharedElementEnterTransition();
        enterTransition.addListener(new Transition.TransitionListener() {
            @Override
            public void onTransitionStart(Transition transition) {}

            @Override
            public void onTransitionEnd(Transition transition) {
                animateRevealShow(bgViewGroup);
            }

            @Override
            public void onTransitionCancel(Transition transition) {}

            @Override
            public void onTransitionPause(Transition transition) {}

            @Override
            public void onTransitionResume(Transition transition) {}
        });
    }

    private void setupExitAnimations() {
        Transition sharedElementReturnTransition = getWindow().getSharedElementReturnTransition();
        sharedElementReturnTransition.setStartDelay(ANIM_DURATION);


        Transition returnTransition = getWindow().getReturnTransition();
        returnTransition.setDuration(ANIM_DURATION);
        returnTransition.addListener(new Transition.TransitionListener() {
            @Override
            public void onTransitionStart(Transition transition) {
                animateRevealHide(bgViewGroup);
            }

            @Override
            public void onTransitionEnd(Transition transition) {}

            @Override
            public void onTransitionCancel(Transition transition) {}

            @Override
            public void onTransitionPause(Transition transition) {}

            @Override
            public void onTransitionResume(Transition transition) {}
        });
    }

    private void animateRevealShow(View viewRoot) {
        int cx = (viewRoot.getLeft() + viewRoot.getRight()) / 2;
        int cy = (viewRoot.getTop() + viewRoot.getBottom()) / 2;
        int finalRadius = Math.max(viewRoot.getWidth(), viewRoot.getHeight());

        Animator anim = ViewAnimationUtils.createCircularReveal(viewRoot, cx, cy, 0, finalRadius);
        viewRoot.setVisibility(View.VISIBLE);
        anim.setDuration(ANIM_DURATION);
        anim.start();
    }

    private void animateRevealHide(final View viewRoot) {
        int cx = (viewRoot.getLeft() + viewRoot.getRight()) / 2;
        int cy = (viewRoot.getTop() + viewRoot.getBottom()) / 2;
        int initialRadius = viewRoot.getWidth();

        Animator anim = ViewAnimationUtils.createCircularReveal(viewRoot, cx, cy, initialRadius, 0);
        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                viewRoot.setVisibility(View.INVISIBLE);
            }
        });
        anim.setDuration(ANIM_DURATION);
        anim.start();
    }

    /** */
    public void displayList(List<M_Name> name) {
        lsName = (RecyclerView) findViewById(R.id.rv_frequency_list);

//        RecyclerView.ItemDecoration itemDecoration =
//                new DividerItemDecoration(this, LinearLayoutManager.VERTICAL);

        lsName.addItemDecoration(new CustomDividerItemDecoration(this, null));
//        lsName.setItemAnimator(new DefaultItemAnimator());

        NameRecycleViewAdapter na = new NameRecycleViewAdapter(this, name);

       final VerticalRecyclerViewFastScroller fastScroller = (VerticalRecyclerViewFastScroller) findViewById(R.id.fast_scroller);

        /* Connect the recycler to the scroller (to let the scroller scroll the list)*/
        fastScroller.setRecyclerView(lsName);
        lsName.addOnScrollListener(fastScroller.getOnScrollListener());

        setRecyclerViewLayoutManager(lsName);
        lsName.setAdapter(na);

    }

    /**
     * Set RecyclerView's LayoutManager
     */
    public void setRecyclerViewLayoutManager(RecyclerView recyclerView) {
        int scrollPosition = 0;

        // If a layout manager has already been set, get current scroll position.
        if (recyclerView.getLayoutManager() != null) {
            scrollPosition =
                    ((LinearLayoutManager) recyclerView.getLayoutManager()).findFirstCompletelyVisibleItemPosition();
        }

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);

        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.scrollToPosition(scrollPosition);
    }

    @Override
    public void onClick(View v) {
        String where = "";
        Cursor c = null;

        switch (v.getId()) {
            case R.id.tvFrequency:

                /** This is will select only those which are not marked */
                where = TableContract.Name.NAME_EN + " like '" + selectedAlphabet + "%' AND " + TableContract.Name.GENDER_CAST
                        + " = ''" + " ORDER BY " + TableContract.Name.NAME_FRE + " DESC";

                c = dbHelper.getTableValue(TableContract.Name.TABLE_NAME, new String[]{TableContract.Name.AUTO_ID,
                        TableContract.Name.NAME_EN, TableContract.Name.NAME_MA, TableContract.Name.NAME_FRE,
                        TableContract.Name.GENDER_CAST}, where);


                break;

            case R.id.tvEnglish:


                /** This is will select only those which are not marked */
                where = TableContract.Name.NAME_EN + " like '" + selectedAlphabet + "%' AND " + TableContract.Name.GENDER_CAST
                        + " = ''" + " ORDER BY " + TableContract.Name.NAME_EN + " ASC";

                c = dbHelper.getTableValue(TableContract.Name.TABLE_NAME, new String[]{TableContract.Name.AUTO_ID,
                        TableContract.Name.NAME_EN, TableContract.Name.NAME_MA, TableContract.Name.NAME_FRE,
                        TableContract.Name.GENDER_CAST}, where);


                break;

            case R.id.tvHindi:


                /** This is will select only those which are not marked */
                where = TableContract.Name.NAME_EN + " like '" + selectedAlphabet + "%' AND " + TableContract.Name.GENDER_CAST
                        + " = ''" + " ORDER BY " + TableContract.Name.NAME_MA + " ASC";

                c = dbHelper.getTableValue(TableContract.Name.TABLE_NAME, new String[]{TableContract.Name.AUTO_ID,
                        TableContract.Name.NAME_EN, TableContract.Name.NAME_MA, TableContract.Name.NAME_FRE,
                        TableContract.Name.GENDER_CAST}, where);


                break;
        }

    }

    /** */
    List<M_Name> parseListName(Cursor c) {
        List<M_Name> lsName = new ArrayList<M_Name>();
        if (c != null && c.getCount() > 0) {
            c.moveToFirst();
            do {
                int id = c.getInt(c.getColumnIndex(TableContract.Name.AUTO_ID));

                String en = c.getString(c.getColumnIndex(TableContract.Name.NAME_EN));
                String ma = c.getString(c.getColumnIndex(TableContract.Name.NAME_MA));
                int fre = c.getInt(c.getColumnIndex(TableContract.Name.NAME_FRE));

                String s = c.getString(c.getColumnIndex(TableContract.Name.GENDER_CAST));

				/* Considering default value as -1 */
                String desc = "-1";
                if (s != null && s.length() > 0)
                    desc = s;

                M_Name temp = new M_Name(ma, en, fre, id, desc);
                lsName.add(temp);
            } while (c.moveToNext());

            /** Close database */
            c.close();
            dbHelper.close();
        }
        return lsName;
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void onBackPressed() {
        dbHelper.close();
        super.onBackPressed();
    }
}
