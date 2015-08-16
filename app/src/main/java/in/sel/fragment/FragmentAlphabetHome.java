package in.sel.fragment;


import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import in.sel.adapter.AlphabetRecyclerViewAdapter;
import in.sel.customview.MarginDecoration;
import in.sel.dbhelper.DBHelper;
import in.sel.dbhelper.TableContract;
import in.sel.framework.OnAlphabetListener;
import in.sel.indianbabyname.ActivityDisplayName;
import in.sel.indianbabyname.ActivityAlphabetMain;
import in.sel.indianbabyname.R;
import in.sel.model.M_AlphaCount;
import in.sel.utility.AppConstants;

public class FragmentAlphabetHome extends Fragment implements OnAlphabetListener {

    private String TAG = getClass().getName();
    private RecyclerView rv;

    public static FragmentAlphabetHome getInstance() {
        FragmentAlphabetHome fragAlpha = new FragmentAlphabetHome();
        return fragAlpha;
    }

    /** */
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_alphabet, container, false);

        /** Enable for Click */
        rv = (RecyclerView) v.findViewById(R.id.rv_alphabet);

        /** Since Recycle View has fixed size of element i.e 26*/
        rv.setHasFixedSize(true);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 3);

        /** Margin Decorator is Class which decide Margin of span*/
        rv.addItemDecoration(new MarginDecoration(getActivity()));
        rv.setLayoutManager(gridLayoutManager);

        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        SparseArray array = getCount();
        AlphabetRecyclerViewAdapter adapter = new AlphabetRecyclerViewAdapter(getActivity(), array, this);
        rv.setAdapter(adapter);

        if (AppConstants.DEBUG)
            Log.e(TAG, "onActivityCreated");
    }

    /**
     * Give count of Remaining Value in each section
     */
    private SparseArray getCount() {
        DBHelper db = new DBHelper(getActivity());
        SparseArray array = new SparseArray(26);
        Cursor c = db.getTableValue(TableContract.CountValue.TABLE_NAME, new String[]{TableContract.CountValue.ALPHABET, TableContract.CountValue.ALPH_COUNT}, null);

        if (c != null && c.getCount() > 0) {
            c.moveToFirst();
            int trace = 0;
            do {
                int alphaCount = c.getInt(c.getColumnIndex(TableContract.CountValue.ALPH_COUNT));
                String alpha = c.getString(c.getColumnIndex(TableContract.CountValue.ALPHABET));

                array.append(trace, new M_AlphaCount(alphaCount, alpha));
                trace++;
            } while (c.moveToNext());
        }
        db.close();
        return array;
    }


    @Override
    public void onAlphaClickListener( View v, final M_AlphaCount mAlphaCount) {

        PropertyValuesHolder scaleX = PropertyValuesHolder.ofFloat(View.SCALE_X, 5);
        PropertyValuesHolder scaleY = PropertyValuesHolder.ofFloat(View.SCALE_Y, 5);
        PropertyValuesHolder alpha = PropertyValuesHolder.ofFloat(View.ALPHA, 0);

        ObjectAnimator anim = ObjectAnimator.ofPropertyValuesHolder(v, scaleX, scaleY, alpha).setDuration(500);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(anim);
        animatorSet.start();

        animatorSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                Intent in = new Intent(getActivity(), ActivityDisplayName.class);
                in.putExtra(ActivityAlphabetMain.SELECTED_ALPHA_BET, mAlphaCount.getAlphabet());
                getActivity().startActivity(in);

                getActivity().overridePendingTransition(0, 0);
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });
    }
}