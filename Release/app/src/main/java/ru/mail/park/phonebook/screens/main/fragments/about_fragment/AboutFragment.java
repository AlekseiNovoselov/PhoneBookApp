package ru.mail.park.phonebook.screens.main.fragments.about_fragment;

import android.net.Uri;
import android.os.Bundle;
import android.support.customtabs.CustomTabsIntent;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.ButterKnife;
import ru.mail.park.phonebook.R;
import ru.mail.park.phonebook.screens.main.fragments.about_fragment.utils.CustomTabActivityHelper;
import ru.mail.park.phonebook.screens.main.fragments.about_fragment.utils.WebviewFallback;

public class AboutFragment extends Fragment {

    CustomTabActivityHelper mCustomTabActivityHelper;

    private static final String URL_NOVOSELOV_PARK_DIPLOMA = "https://park.mail.ru/alumni/11/417/";

    public static AboutFragment newInstance() {
        AboutFragment aboutFragment = new AboutFragment();
        Bundle arguments = new Bundle();
        aboutFragment.setArguments(arguments);
        return aboutFragment;
    }

    @Override
    public void onStart() {
        super.onStart();
        mCustomTabActivityHelper.bindCustomTabsService(getActivity());
    }

    @Override
    public void onStop() {
        super.onStop();
        mCustomTabActivityHelper.unbindCustomTabsService(getActivity());
    }
    private void setupCustomTabHelper() {
        mCustomTabActivityHelper = new CustomTabActivityHelper();
        mCustomTabActivityHelper.mayLaunchUrl(Uri.parse(URL_NOVOSELOV_PARK_DIPLOMA), null, null);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(getActivity());
        setupCustomTabHelper();
        mCustomTabActivityHelper = new CustomTabActivityHelper();

    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_about, null);
        TextView aboutTextView = (TextView) v.findViewById(R.id.about_text_view);
        Spanned aboutText = Html.fromHtml(getString(R.string.about_text));
        aboutTextView.setText(aboutText);

        TextView tvLink = (TextView) v.findViewById(R.id.profile_link);
        Spanned tvLinkText = Html.fromHtml(getString(R.string.profile_link_text));
        tvLink.setText(tvLinkText);

        tvLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // http://blog.grafixartist.com/google-chrome-custom-tabs-android-tutorial/
                // Fallback? No problem!
                // The good news is, we just need to worry about launching the Chrome Custom Tab.
                // The support library takes care of the fallback for us.
                // Which means, if the user doesn’t have Chrome, it will load the same in a WebView. Neat!

                openCustomTab();
            }
        });

        return v;
    }

    private void openCustomTab() {
        CustomTabsIntent.Builder intentBuilder = new CustomTabsIntent.Builder();
        CustomTabActivityHelper.openCustomTab(
                getActivity(), intentBuilder.build(), Uri.parse(URL_NOVOSELOV_PARK_DIPLOMA), new WebviewFallback());
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    }
}
