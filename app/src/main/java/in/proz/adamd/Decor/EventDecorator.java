package in.proz.adamd.Decor;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.text.style.ForegroundColorSpan;
import android.util.Log;

import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.CalendarDay;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import in.proz.adamd.R;

public class EventDecorator implements DayViewDecorator  {

    // Map to store each date and its associated color
    private final Map<CalendarDay, Integer> dateColorMap;

    // Constructor that accepts a collection of CalendarDay and corresponding colors
    public EventDecorator(Collection<CalendarDay> dates, Collection<Integer> colors) {
        if (dates.size() != colors.size()) {
            throw new IllegalArgumentException("Dates and colors collections must have the same size.");
        }

        dateColorMap = new HashMap<>();
        // Assuming that dates and colors are paired in order
        int index = 0;
        for (CalendarDay date : dates) {
            Log.d("getColot"," clr "+colors.toArray()[index]+" date "+date);
            dateColorMap.put(date, ((Integer) colors.toArray()[index]));
            index++;
        }
    }

    @Override
    public boolean shouldDecorate(CalendarDay day) {
        // Check if the day exists in our map
        return dateColorMap.containsKey(day);
    }

    @Override
    public void decorate(DayViewFacade view) {

        // Retrieve the color for the day
    /*   CalendarDay day = view.getDate();
        if (dateColorMap.containsKey(day)) {
            int color = dateColorMap.get(day);
            // Add the dot with the corresponding color
            view.addSpan(new DotSpan(5, color));
        }
*/


        int k=0;
        for (Map.Entry<CalendarDay, Integer> entry : dateColorMap.entrySet()) {
            CalendarDay day = entry.getKey();
            if (shouldDecorate(day)) {
                int col = dateColorMap.get(day);

                String colorString = String.format("#%06X", (0xFFFFFF & col));  // Converts the integer to a hex string

                Log.d("getColot"," colot  "+entry.getValue()+" arr "+col+"  "+day+" color code "+colorString);

                // Add the dot with the corresponding color
              //  view.addSpan(new DotSpan(10, entry.getValue())); // 10 is the radius of the dot, change as needed
                view.addSpan(new ForegroundColorSpan(Color.WHITE)); // This changes the text color to white
                 GradientDrawable drawable = new GradientDrawable();
                drawable.setColor(Color.parseColor(colorString));

                // Set background color

                drawable.setStroke(5, Color.WHITE);
                 drawable.setCornerRadius(5); // Set corner radius for rounded corners
                drawable.setSize(27, 27); // Set size here (width and height in pixels)
                // Apply the drawable as the background

                view.setBackgroundDrawable(drawable);
               //    view.addSpan(new DotSpan(5, entry.getValue()));
            }
        }
    }
}
