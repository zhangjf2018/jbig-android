package lib.jacob.org.jbigandroid.fragment;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.FrameLayout;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;
import lib.jacob.org.jbigandroid.JbigApplication;
import lib.jacob.org.jbigandroid.R;
import lib.jacob.org.jbigandroid.controller.JbigController;
import lib.jacob.org.jbigandroid.realmobj.JbigItem;
import lib.jacob.org.jbigandroid.utils.ByteUtils;
import lib.jacob.org.jbigandroid.widget.PaintView;
import lib.jacob.org.lib.JbigCodec;
import lib.jacob.org.lib.JbigCodecFactory;

public class PaintViewFragment extends Fragment implements
        EncoderDialogFragment.EncodeDialogListener,
        JbigController.JbigEncoderUi {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private PaintView mPaintView;

    @Bind(R.id.encoder)
    Button mEncodeBtn;

    @Bind(R.id.clear)
    Button mClearBtn;

    @Bind(R.id.paint_content)
    FrameLayout mFrameLayout;

    private JbigController.JbigUiCallback mJbigUiCallback;
    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PaintViewFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PaintViewFragment newInstance(String param1, String param2) {
        PaintViewFragment fragment = new PaintViewFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public PaintViewFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_paint_view, container, false);
        ButterKnife.bind(this, view);

        mEncodeBtn.setEnabled(false);
        mPaintView = new PaintView(getActivity());
        AbsListView.LayoutParams params = new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        mPaintView.setLayoutParams(params);
        mPaintView.requestFocus();
        mPaintView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    mEncodeBtn.setEnabled(true);
                }

                return false;
            }
        });

        mFrameLayout.addView(mPaintView);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        getController().attachUi(this);
    }

    @Override
    public void onPause() {
        getController().detachUi(this);

        super.onPause();
    }

    @Override
    public void onDestroyView() {
        ButterKnife.unbind(this);

        super.onDestroyView();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    // onClick listener
    @OnClick(R.id.encoder)
    public void onEncodeClicked() {
        //TODO:XXX pop up a dialog, clean the PaintView.

        showEncodeDialog();
    }

    @OnClick(R.id.clear)
    public void onClearBtnClicked() {
        mPaintView.clear();
        mEncodeBtn.setEnabled(false);
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        encodeAndSave();
        onClearBtnClicked();
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
        Log.e("TAG", "onDialog Negative Clicked");
    }

    private void showEncodeDialog() {
        EncoderDialogFragment dialogFragment = new EncoderDialogFragment();
        dialogFragment.setEncodeDialogListener(this);
        dialogFragment.show(getChildFragmentManager(), "encode");
    }

    private void encodeAndSave() {
        Bitmap bitmap = mPaintView.getCachebBitmap();

        Bitmap[] bitmaps = new Bitmap[1];
        bitmaps[0] = bitmap;

        JbigCodec jbigCodec = JbigCodecFactory.getJbigCodec(JbigCodecFactory.CODEC.JNI_CODEC);

        if (jbigCodec != null) {
            byte[] jbigData = jbigCodec.encode(bitmaps);

            Realm realm = Realm.getInstance(getActivity());
            realm.beginTransaction();

            JbigItem item = realm.createObject(JbigItem.class);
            item.setTag("PaintView");
            item.setJbig(jbigData);

            realm.commitTransaction();

            String serializedJbig = ByteUtils.byteArray2HexString(jbigData);
            Log.e("Encode", serializedJbig);
        }
    }

    // JbigEncoderUi
    @Override
    public void setCallback(JbigController.JbigUiCallback callback) {
        mJbigUiCallback = callback;
    }

    @Override
    public boolean isModal() {
        return false;
    }

    private JbigController getController() {
        return JbigApplication.from(getActivity()).getMainController().getJbigController();
    }
}
