package ch.epfl.favo.util;

import android.location.LocationManager;
import android.location.LocationProvider;
import android.view.SurfaceControl;
import android.view.View;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import org.junit.Test;
import org.junit.function.ThrowingRunnable;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.junit.Assert.assertThrows;
import static junit.framework.TestCase.assertNotNull;
import static org.junit.Assert.assertNotEquals;
import static junit.framework.TestCase.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static junit.framework.TestCase.assertEquals;

import ch.epfl.favo.common.NoPositionFoundException;
import ch.epfl.favo.map.GpsTracker;
import ch.epfl.favo.util.CommonTools;

import static org.junit.Assert.assertEquals;

public class CommonToolsTests {
    CommonTools commonTools = new CommonTools();
    @Test
    public void ConvertTimeTest(){
        long date = new Date().getTime();
        Format format = new SimpleDateFormat("yyyy MM dd HH:mm:ss");
        String time = format.format(date);
        assertEquals(CommonTools.convertTime(date), time);
    }
}
