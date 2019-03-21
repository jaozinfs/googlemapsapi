package findsolucoes.com.appcidade.Utils.googlemapsapi.EstimatedTime;

import org.freetime.mob.webservices.RequestResult;

import java.io.IOException;

public interface CalculateEstimatedTimeListener {
    void onEstimatedTimeResult(EstimatedtimeResponse result);
    void onEstimatedTimeError(Exception e);
}
