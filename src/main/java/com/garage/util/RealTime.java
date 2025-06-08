package com.garage.util;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.Locale;
import java.util.Objects;

import com.garage.entity.Lease;
import com.garage.repository.SpotRepository;

public class RealTime {

    public static BigDecimal getPaymentValue(Lease lease) {

        if (Objects.nonNull(lease) && Objects.nonNull(lease.getGarage())) {
            BigDecimal basePriceRuleCapacity = lease.getCalculationRuleCapacity().divide(new BigDecimal(100)).multiply(lease.getGarage().getBasePrice());

            BigDecimal priceHourEntry = lease.getGarage().getBasePrice().add( basePriceRuleCapacity);

            long HOURS = Math.abs( Objects.nonNull(lease.getExitTime())? lease.getExitTime().until(lease.getEntryTime(), ChronoUnit.HOURS): LocalDateTime.now().until(lease.getEntryTime(), ChronoUnit.HOURS));

            if (HOURS >= 1) {
                if (HOURS <= 1) {
                    return priceHourEntry;
                } else {
                    BigDecimal amount = lease.getGarage().getBasePrice().multiply(new BigDecimal(HOURS - 1)).add(priceHourEntry);
                    return amount;
                }
            }
        }

        return BigDecimal.ZERO;
    }

    public static LocalTime getDduration(Lease lease) {
        if (Objects.nonNull(lease)) {
            int HOUR = Math.abs((int) lease.getExitTime().until(lease.getEntryTime(), ChronoUnit.HOURS));
            int MINUTES = (int) (Math.abs(lease.getExitTime().until(lease.getEntryTime(), ChronoUnit.MINUTES)) - (HOUR * 60));

            return LocalTime.of(HOUR, MINUTES, 0);
        }
        
        return LocalTime.of(0, 0, 0);
    }

    public static LocalDateTime getDuration(Lease lease) {
        if (Objects.nonNull(lease)) {
            int HOUR = Math.abs((int) LocalDateTime.now().until(lease.getEntryTime(), ChronoUnit.HOURS));
            int MINUTES = (int) (Math.abs(LocalDateTime.now().until(lease.getEntryTime(), ChronoUnit.MINUTES)) - (HOUR * 60));

            return LocalDateTime.of(LocalDateTime.now().getYear(), LocalDateTime.now().getMonth(), LocalDateTime.now().getDayOfMonth(), HOUR, MINUTES);
        }

        return LocalDateTime.of(LocalDateTime.now().getYear(), LocalDateTime.now().getMonth(), LocalDateTime.now().getDayOfMonth(), 0, 0);
    }
    
    public static BigDecimal getCalculationRuleCapacity(Long countOcupied, Long countTotal) {
        /***
         * 1. Com lotação menor que 25%, desconto de 10% no preço, na hora da entrada.
         * 2. Com lotação menor até 50%, desconto de 0% no preço, na hora da entrada.
         * 3. Com lotação menor até 75%, aumentar o preço em 10%, na hora da entrada.
         * 4. Com lotação menor até 100%, aumentar o preço em 25%, na hora da entrada.
         */
        BigDecimal lotation = new BigDecimal(( countOcupied * 100) / countTotal);
        if (lotation.floatValue() < 25)
            return new BigDecimal(-10);
        if (lotation.floatValue() >= 25 && lotation.floatValue() <= 50)
            return new BigDecimal(0);
        if (lotation.floatValue() > 50 && lotation.floatValue() <= 75)
            return new BigDecimal(10);

        return new BigDecimal(25);

    }
}
