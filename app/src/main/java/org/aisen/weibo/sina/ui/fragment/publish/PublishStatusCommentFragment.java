package org.aisen.weibo.sina.ui.fragment.publish;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;

import org.aisen.android.component.bitmaploader.BitmapLoader;
import org.aisen.android.network.http.Params;
import org.aisen.android.support.inject.ViewInject;
import org.aisen.android.ui.activity.basic.BaseActivity;
import org.aisen.android.ui.fragment.ABaseFragment;
import org.aisen.weibo.sina.R;
import org.aisen.weibo.sina.sinasdk.bean.StatusContent;
import org.aisen.weibo.sina.support.bean.PublishBean;
import org.aisen.weibo.sina.support.bean.PublishBean.PublishStatus;
import org.aisen.weibo.sina.support.bean.PublishType;
import org.aisen.weibo.sina.support.utils.AisenUtils;
import org.aisen.weibo.sina.support.utils.ImageConfigUtils;
import org.aisen.weibo.sina.support.utils.UMengUtil;

/**
 * 评论微博
 * 
 * @author wangdan
 *
 */
public class PublishStatusCommentFragment extends APublishFragment implements OnCheckedChangeListener {

	public static ABaseFragment newInstance(PublishBean bean) {
		PublishStatusCommentFragment fragment = new PublishStatusCommentFragment();
		
		Bundle args = new Bundle();
		args.putSerializable("bean", bean);
		fragment.setArguments(args);
		
		return fragment;
	}
	
	@ViewInject(id = R.id.imgPhoto)
	ImageView imgPhoto;
	
	@Override
	protected void layoutInit(LayoutInflater inflater, Bundle savedInstanceState) {
		super.layoutInit(inflater, savedInstanceState);
		
		btnCamera.setVisibility(View.GONE);
		btnOverflow.setVisibility(View.GONE);
		
		editContent.setHint(R.string.publish_cmt_def);
		
		StatusContent status = getPublishBean().getStatusContent();
		txtContent.setText(AisenUtils.getCommentText(status.getText()));
		if (status.getUser() != null)
			BitmapLoader.getInstance().display(this, AisenUtils.getUserPhoto(status.getUser()), imgPhoto, ImageConfigUtils.getLargePhotoConfig());

		checkBox.setChecked(Boolean.parseBoolean(getPublishBean().getExtras().getParameter("forward")));
		checkBox.setOnCheckedChangeListener(this);

        ((BaseActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((BaseActivity) getActivity()).getSupportActionBar().setTitle(R.string.title_cmt_status);
	}
	
	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		getPublishBean().getExtras().addParameter("forward", String.valueOf(isChecked));
	}
	
	@Override
	PublishBean newPublishBean() {
		return null;
	}
	
	public static PublishBean generateBean(StatusContent status) {
		PublishBean bean = new PublishBean();
		bean.setStatusContent(status);
		
		bean.setType(PublishType.commentCreate);
		bean.setStatus(PublishStatus.create);
		
		bean.setParams(new Params());
		bean.getParams().addParameter("id", status.getId() + "");
		// 当评论转发微博时，是否评论给原微博，0：否、1：是，默认为0。
		bean.getParams().addParameter("comment_ori", "0");

		bean.setExtras(new Params());
		// 评论是否同时转发
		bean.getExtras().addParameter("forward", "false");
		
		return bean;
	}

	@Override
	boolean checkValid(PublishBean bean) {
		String content = editContent.getText().toString();
		
		if (TextUtils.isEmpty(content)) {
			showMessage(R.string.error_none_comment);
			return false;
		}
		
		// comment
		bean.getParams().addParameter("comment", content);
		
		return true;
	}

	@Override
	public int inflateContentView() {
		return R.layout.ui_publish_status_comment;
	}

	@Override
	public void onResume() {
		super.onResume();

		UMengUtil.onPageStart(getActivity(), "发布评论微博页");
	}

	@Override
	public void onPause() {
		super.onPause();

		UMengUtil.onPageEnd(getActivity(), "发布评论微博页");
	}

}
