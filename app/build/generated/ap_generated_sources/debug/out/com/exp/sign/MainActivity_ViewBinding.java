// Generated code from Butter Knife. Do not modify!
package com.exp.sign;

import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import butterknife.Unbinder;
import butterknife.internal.DebouncingOnClickListener;
import butterknife.internal.Utils;
import com.wang.avi.AVLoadingIndicatorView;
import java.lang.IllegalStateException;
import java.lang.Override;

public class MainActivity_ViewBinding implements Unbinder {
  private MainActivity target;

  private View view2131230770;

  private View view2131230836;

  @UiThread
  public MainActivity_ViewBinding(MainActivity target) {
    this(target, target.getWindow().getDecorView());
  }

  @UiThread
  public MainActivity_ViewBinding(final MainActivity target, View source) {
    this.target = target;

    View view;
    target.etUsername = Utils.findRequiredViewAsType(source, R.id.et_username, "field 'etUsername'", EditText.class);
    target.etPassword = Utils.findRequiredViewAsType(source, R.id.et_password, "field 'etPassword'", EditText.class);
    view = Utils.findRequiredView(source, R.id.bt_go, "field 'btGo' and method 'onClick'");
    target.btGo = Utils.castView(view, R.id.bt_go, "field 'btGo'", Button.class);
    view2131230770 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.onClick(p0);
      }
    });
    target.cv = Utils.findRequiredViewAsType(source, R.id.cv, "field 'cv'", CardView.class);
    view = Utils.findRequiredView(source, R.id.fab, "field 'fab' and method 'onClick'");
    target.fab = Utils.castView(view, R.id.fab, "field 'fab'", FloatingActionButton.class);
    view2131230836 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.onClick(p0);
      }
    });
    target.rbpt = Utils.findRequiredViewAsType(source, R.id.putong, "field 'rbpt'", RadioButton.class);
    target.rbgl = Utils.findRequiredViewAsType(source, R.id.guanli, "field 'rbgl'", RadioButton.class);
    target.loadingIndicatorView = Utils.findRequiredViewAsType(source, R.id.loading, "field 'loadingIndicatorView'", AVLoadingIndicatorView.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    MainActivity target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.etUsername = null;
    target.etPassword = null;
    target.btGo = null;
    target.cv = null;
    target.fab = null;
    target.rbpt = null;
    target.rbgl = null;
    target.loadingIndicatorView = null;

    view2131230770.setOnClickListener(null);
    view2131230770 = null;
    view2131230836.setOnClickListener(null);
    view2131230836 = null;
  }
}
