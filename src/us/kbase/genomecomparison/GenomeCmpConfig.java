package us.kbase.genomecomparison;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Map;

import us.kbase.auth.AuthToken;
import us.kbase.auth.TokenFormatException;
import us.kbase.common.service.JsonClientException;
import us.kbase.common.service.Tuple11;
import us.kbase.common.service.UnauthorizedException;
import us.kbase.userandjobstate.InitProgress;
import us.kbase.userandjobstate.Results;
import us.kbase.userandjobstate.UserAndJobStateClient;
import us.kbase.workspace.ObjectData;
import us.kbase.workspace.ObjectIdentity;
import us.kbase.workspace.SaveObjectsParams;
import us.kbase.workspace.WorkspaceClient;

public class GenomeCmpConfig {
	private int threadCount;
	private File tempDir;
	private File blastBin;
	private ObjectStorage objectStorage;
	private JobStatuses jobStatuses;

	private String wsUrl = "http://dev04.berkeley.kbase.us:7058";  // "https://kbase.us/services/ws/";
    private String jobSrvUrl = "https://kbase.us/services/UserAndJobState/";

	public GenomeCmpConfig() {
		this(1, null, null, null, null);
	}
	
	public GenomeCmpConfig(int threadCount, File tempDir, File blastBin, ObjectStorage objectStorage, JobStatuses jobStatuses) {
		this.threadCount = threadCount;
		this.tempDir = tempDir;
		this.blastBin = blastBin;
		this.objectStorage = objectStorage != null ? objectStorage : new ObjectStorage() {
			@Override
			public List<ObjectData> getObjects(String token, List<ObjectIdentity> objectIds) throws Exception {
				return createWsClient(token).getObjects(objectIds);
			}
			@Override
			public List<Tuple11<Long, String, String, String, Long, String, Long, String, String, Long, Map<String, String>>> saveObjects(
					String token, SaveObjectsParams params) throws Exception {
				return createWsClient(token).saveObjects(params);
			}
		};
		this.jobStatuses = jobStatuses != null ? jobStatuses : new JobStatuses() {
			@Override
			public String createAndStartJob(String token, String status, String desc,
					InitProgress progress, String estComplete) throws IOException, JsonClientException {
				return createJobClient(token).createAndStartJob(token, status, desc, progress, estComplete);
			}
			@Override
			public void updateJob(String job, String token, String status, String estComplete) throws IOException, JsonClientException {
				createJobClient(token).updateJob(job, token, status, estComplete);
			}
			@Override
			public void completeJob(String job, String token, String status,
					String error, Results res) throws IOException, JsonClientException {
				createJobClient(token).completeJob(job, token, status, error, res);
			}
		};
	}
	
	public String getWsUrl() {
		return wsUrl;
	}
	
	public void setWsUrl(String wsUrl) {
		this.wsUrl = wsUrl;
	}
	
	public String getJobSrvUrl() {
		return jobSrvUrl;
	}
	
	public void setJobSrvUrl(String jobSrvUrl) {
		this.jobSrvUrl = jobSrvUrl;
	}
	
	public WorkspaceClient createWsClient(String token) throws Exception {
		WorkspaceClient ret = new WorkspaceClient(new URL(wsUrl), new AuthToken(token));
		ret.setAuthAllowedForHttp(true);
		return ret;
	}

	public UserAndJobStateClient createJobClient(String token) throws IOException, JsonClientException {
		try {
			UserAndJobStateClient ret = new UserAndJobStateClient(new URL(jobSrvUrl), new AuthToken(token));
			ret.setAuthAllowedForHttp(true);
			return ret;
		} catch (TokenFormatException e) {
			throw new JsonClientException(e.getMessage(), e);
		} catch (UnauthorizedException e) {
			throw new JsonClientException(e.getMessage(), e);
		}
	}

	public int getThreadCount() {
		return threadCount;
	}
	
	public File getTempDir() {
		return tempDir;
	}
	
	public File getBlastBin() {
		return blastBin;
	}
	
	public ObjectStorage getObjectStorage() {
		return objectStorage;
	}
	
	public JobStatuses getJobStatuses() {
		return jobStatuses;
	}
}
