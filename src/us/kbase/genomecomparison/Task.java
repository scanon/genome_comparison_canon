package us.kbase.genomecomparison;

public class Task {
	private String jobId;
	private BlastProteomesParams params;
	private String authToken;
	
	public Task(String jobId, BlastProteomesParams params, String authToken) {
		this.jobId = jobId;
		this.params = params;
		this.authToken = authToken;
	}
	
	public String getJobId() {
		return jobId;
	}
	
	public BlastProteomesParams getParams() {
		return params;
	}
	
	public String getAuthToken() {
		return authToken;
	}
}
