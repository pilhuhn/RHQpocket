package org.rhq.pocket.helper;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.rhq.core.domain.measurement.MeasurementUnits;

public class MetricsUnitConverter {

    public static String scaleValue(Double x, MeasurementUnits mu) {
        if (mu == MeasurementUnits.NONE)
            return x.toString();  // TODO get some surrogates so that the string won't be too long

        if (mu == MeasurementUnits.PERCENTAGE) { // THose are in the 0..1 range, scale up to be readable
            x = 100 * x;
            return String.format("%.1f%%", x);
        }

//        System.out.println(x);
        if (x.isInfinite() || x.isNaN())
            return "??";

        BigDecimal bd = BigDecimal.valueOf(x);
//        System.out.println(bd.scale());
//        System.out.println(bd.precision());
        int vorkomma = bd.precision() - bd.scale();
        int dreier = vorkomma / 3;
        BigDecimal bd2 = bd.movePointLeft(dreier * 3);
//        System.out.println(bd2);
        BigDecimal bd3 = bd2.setScale(2, BigDecimal.ROUND_HALF_DOWN);
//        System.out.println(bd3);

        List<MeasurementUnits> matching = new ArrayList<MeasurementUnits>();
        for (MeasurementUnits mu2 : MeasurementUnits.values()) {
            if (!mu2.isComparableTo(mu))
                continue;
            matching.add(mu2);
        }

        MeasurementUnits targetUnit = null;
        for (int i = 0; i < matching.size(); i++) {
            if (matching.get(i).equals(mu)) {
                targetUnit = matching.get(i + dreier);
                break;
            }
        }
        if (targetUnit == null) {
            System.err.println("No target unit found");
            targetUnit = mu.getBaseUnits();
        }
//        System.out.println("Target unit " + targetUnit.toString());
        double d = MeasurementUnits.scaleUp(x, targetUnit);
//        System.out.println(d);

        String result = bd3.toString() + " " + targetUnit.toString();

//        System.out.println("---");
        return result;
    }
}