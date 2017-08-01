package com.imgpre;

import android.content.Context;
import android.os.Handler;
import android.text.Layout;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.TextView;

import cn.isif.alibs.utils.phone.DeviceUtil;


public class ReadMoreUtil {


    /**
     * 添加showmore和show more的点击事件
     */
    public static void limitStringTo140(Context context, String summerText, final TextView textView, int maxLine, final View.OnClickListener clickListener) {
        if (textView == null) return;
        textView.setOnClickListener(null);
//        int width = textView.getWidth();
//        if (width == 0) {
//            int w_screen = (int) DeviceUtil.getWidth(context);
//            width = w_screen - dip2px(context, 24);
//        }
        int w_screen = (int) DeviceUtil.getWidth(context);
        int width = w_screen - dip2px(context, 24);

        int lineCount = lineCount(textView, summerText, width);
        if (lineCount <= maxLine) {
            textView.setText(summerText);
            return;
        }

        //如果超出了行数限制
        int lastCharIndex = getLastCharIndexForLimitTextView(textView, summerText, width, maxLine);
        textView.setMovementMethod(LinkMovementMethod.getInstance());
        String explicitText = null;
        if (summerText.charAt(lastCharIndex) == '\n') {
            explicitText = summerText.substring(0, lastCharIndex);
        } else if (lastCharIndex > 12) {
            explicitText = summerText.substring(0, lastCharIndex - 12);
        }
        int sourceLength = explicitText.length();
        String showmore = "显示全部";
        explicitText = explicitText + "..." + showmore;
        final SpannableString mSpan = new SpannableString(explicitText);
        final String finalSummerize = summerText;
        mSpan.setSpan(new ClickableSpan() {
            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(textView.getResources().getColor(R.color.color_read_more));
                ds.setAntiAlias(true);
                ds.setUnderlineText(false);
            }

            @Override
            public void onClick(View widget) {
                //显示全部文字
                textView.setText(finalSummerize);
                textView.setOnClickListener(null);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (clickListener != null)
                            textView.setOnClickListener(clickListener);
                    }
                }, 20);
            }
        }, sourceLength, explicitText.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        textView.setText(mSpan);
    }

    /**
     * 获取指定行最后文字的位置
     */
    public static int getLastCharIndexForLimitTextView(TextView textView, String content, int width, int maxLine) {
        TextPaint textPaint = textView.getPaint();
        StaticLayout staticLayout = new StaticLayout(content, textPaint, width, Layout.Alignment.ALIGN_NORMAL, 1, 0, false);
        if (staticLayout.getLineCount() > maxLine)
            return staticLayout.getLineStart(maxLine) - 1;
        else return -1;
    }

    /**
     * 获取文本行数
     */
    public static int lineCount(TextView textView, String content, int width) {
        TextPaint textPaint = textView.getPaint();
        StaticLayout staticLayout = new StaticLayout(content, textPaint, width, Layout.Alignment.ALIGN_NORMAL, 1, 0, false);
        return staticLayout.getLineCount();
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }
}
