package us.kbase.genomecomparison;

import java.io.IOException;

import us.kbase.common.service.JsonClientException;
import us.kbase.userandjobstate.InitProgress;
import us.kbase.userandjobstate.Results;

public interface JobStatuses {
    public String createAndStartJob(String token, String status, String desc, InitProgress progress, String estComplete) throws IOException, JsonClientException;
    public void updateJob(String job, String token, String status, String estComplete) throws IOException, JsonClientException;
    public void completeJob(String job, String token, String status, String error, Results res) throws IOException, JsonClientException;
}
