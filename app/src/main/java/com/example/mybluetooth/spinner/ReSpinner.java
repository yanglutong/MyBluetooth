package com.example.mybluetooth.spinner;
/**Android Spinner重复选中某一项获取监听的方法
 * @description
 * @param
 * @return
 * @author lutong
 * @time 2021/12/28 10:31
 */

import android.content.Context;
    import android.util.AttributeSet;
public class ReSpinner extends androidx.appcompat.widget.AppCompatSpinner {
        public boolean isDropDownMenuShown=false;//标志下拉列表是否正在显示
    
        public ReSpinner(Context context) {
            super(context);
        }
    
        public ReSpinner(Context context, AttributeSet attrs) {
            super(context, attrs);
        }
    
        public ReSpinner(Context context, AttributeSet attrs, int defStyle) {
            super(context, attrs, defStyle);
        }
    
        @Override
        public void
        setSelection(int position, boolean animate) {
            boolean sameSelected = position == getSelectedItemPosition();
            super.setSelection(position, animate);
            if (sameSelected) {
                // 如果选择项是Spinner当前已选择的项,则 OnItemSelectedListener并不会触发,因此这里手动触发回调
                getOnItemSelectedListener().onItemSelected(this, getSelectedView(), position, getSelectedItemId());
            }
        }
    
        @Override
        public boolean performClick() {
            this.isDropDownMenuShown = true;
            return super.performClick();
        }
    
        public boolean isDropDownMenuShown(){
            return isDropDownMenuShown;
        }
    
        public void setDropDownMenuShown(boolean isDropDownMenuShown){
            this.isDropDownMenuShown=isDropDownMenuShown;
        }
    
        @Override
        public void
        setSelection(int position) {
            boolean sameSelected = position == getSelectedItemPosition();
            super.setSelection(position);
            if (sameSelected) {
                getOnItemSelectedListener().onItemSelected(this, getSelectedView(), position, getSelectedItemId());
            }
        }
    
        @Override
        public void onDetachedFromWindow() {
            super.onDetachedFromWindow();
        }
    }