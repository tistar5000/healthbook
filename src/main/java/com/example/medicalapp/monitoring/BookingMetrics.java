package com.example.medicalapp.monitoring;

import org.springframework.stereotype.Component;
import java.util.concurrent.atomic.AtomicLong;

@Component
public class BookingMetrics {

    private final AtomicLong totalBookings       = new AtomicLong(0);
    private final AtomicLong failedBookings      = new AtomicLong(0);
    private final AtomicLong notifFailures       = new AtomicLong(0);
    private final AtomicLong totalLatencyMs      = new AtomicLong(0);

    public void recordSuccess(long latencyMs) {
        totalBookings.incrementAndGet();
        totalLatencyMs.addAndGet(latencyMs);
    }

    public void recordFailure()            { failedBookings.incrementAndGet(); }
    public void recordNotifFailure()       { notifFailures.incrementAndGet(); }

    public long getTotalBookings()         { return totalBookings.get(); }
    public long getFailedBookings()        { return failedBookings.get(); }
    public long getNotifFailures()         { return notifFailures.get(); }

    public long getAverageLatencyMs() {
        long total = totalBookings.get();
        return total == 0 ? 0 : totalLatencyMs.get() / total;
    }
}