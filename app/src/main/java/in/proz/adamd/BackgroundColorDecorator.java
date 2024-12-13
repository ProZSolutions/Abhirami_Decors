package in.proz.adamd;


import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.text.style.ForegroundColorSpan;
import android.util.Log;

import java.util.Set;

public class BackgroundColorDecorator implements DayViewDecorator {

    private final Set<CalendarDay> dates;
    private final int color;

    public BackgroundColorDecorator(Set<CalendarDay> dates, int color) {
        this.dates = dates;
        this.color = color;
    }

    @Override
    public boolean shouldDecorate(CalendarDay day) {
        return dates.contains(day);
    }

    @Override
    public void decorate(DayViewFacade view) {

        Log.d("getsizeList"," color "+color);
         // Set the background color
         view.addSpan(new ForegroundColorSpan(Color.WHITE));
        GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(color); // Set background color
        drawable.setStroke(5, Color.WHITE);
        drawable.setSize(30, 30); // Set size here (width and height in pixels)

        drawable.setCornerRadius(5); // Set corner radius for rounded corners
         // Apply the drawable as the background                drawable.setStroke(5, Color.WHITE);
        view.setBackgroundDrawable(drawable);
        //view.setBackgroundDrawable(new ColorDrawable(color));
    }
}
