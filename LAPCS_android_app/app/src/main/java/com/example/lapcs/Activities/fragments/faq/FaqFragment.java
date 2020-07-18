package com.example.lapcs.Activities.fragments.faq;

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.lapcs.R;


public class FaqFragment extends Fragment {



    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_faq, container, false);
        final TextView textView = root.findViewById(R.id.tv_textshare);

        final Typeface typeface = ResourcesCompat.getFont(getContext(), R.font.calibri_regular);

        TextView tv_question_no_1_str = (TextView) root.findViewById(R.id.tv_question_no_1_str);
        tv_question_no_1_str.setTypeface(typeface);
        //tv_question_no_1_str.setTypeface(tv_question_no_1_str.getTypeface(), Typeface.BOLD);

        TextView tv_question_no_2_str = (TextView) root.findViewById(R.id.tv_question_no_2_str);
        tv_question_no_2_str.setTypeface(typeface);

        TextView tv_question_no_3_str = (TextView) root.findViewById(R.id.tv_question_no_3_str);
        tv_question_no_3_str.setTypeface(typeface);

        TextView tv_question_no_4_str = (TextView) root.findViewById(R.id.tv_question_no_4_str);
        tv_question_no_4_str.setTypeface(typeface);

        TextView tv_question_no_5_str = (TextView) root.findViewById(R.id.tv_question_no_5_str);
        tv_question_no_5_str.setTypeface(typeface);

        TextView tv_answer_no_1_str = (TextView) root.findViewById(R.id.tv_answer_no_1_str);
        tv_answer_no_1_str.setTypeface(typeface);

        TextView tv_answer_no_2_str = (TextView) root.findViewById(R.id.tv_answer_no_2_str);
        tv_answer_no_2_str.setTypeface(typeface);

        TextView tv_answer_no_3_str = (TextView) root.findViewById(R.id.tv_answer_no_3_str);
        tv_answer_no_3_str.setTypeface(typeface);

        TextView tv_answer_no_4_str = (TextView) root.findViewById(R.id.tv_answer_no_4_str);
        tv_answer_no_4_str.setTypeface(typeface);

        TextView tv_answer_no_5_str = (TextView) root.findViewById(R.id.tv_answer_no_5_str);
        tv_answer_no_5_str.setTypeface(typeface);







        return root;
    }
}