package com.kingphung.voucher;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.squareup.picasso.Picasso;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link VoucherFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link VoucherFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class VoucherFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String ARG_PARAM3 = "param3";
    private static final String ARG_PARAM4 = "param4";

    private TextView tvTitle, tvCode, tvDescription;
    private ImageView ivImage;

    // TODO: Rename and change types of parameters
    private String title, code, description, img_url;

    private OnFragmentInteractionListener mListener;

    public VoucherFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *

     * @return A new instance of fragment VoucherFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static VoucherFragment newInstance(String title, String code, String description, String img_url) {
        VoucherFragment fragment = new VoucherFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, title);
        args.putString(ARG_PARAM2, code);
        args.putString(ARG_PARAM3, description);
        args.putString(ARG_PARAM4, img_url);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            title = getArguments().getString(ARG_PARAM1);
            code = getArguments().getString(ARG_PARAM2);
            description = getArguments().getString(ARG_PARAM3);
            img_url = getArguments().getString(ARG_PARAM4);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_voucher, container, false);
        tvTitle = view.findViewById(R.id.tvTitle);
        tvCode = view.findViewById(R.id.tvCode);
        tvDescription = view.findViewById(R.id.tvDescription);
        ivImage = view.findViewById(R.id.ivImage);

        tvTitle.setText(title);
        tvCode.setText(code);
        tvDescription.setText(description);
        Picasso.get().load(img_url).into(ivImage);
        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
