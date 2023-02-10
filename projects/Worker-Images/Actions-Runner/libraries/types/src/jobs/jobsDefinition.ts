import {JobDefinition} from "./jobDefinition";
import {RemoteJobDefinition} from "./remoteJobDefinition";

export type JobId = string;
export type JobsDefinition = {
    /**
     * Use jobs.<job_id> to give your job a unique identifier. The key job_id is a string and its value is a map of
     * the job's configuration data. You must replace <job_id> with a string that is unique to the jobs object.
     * The <job_id> must start with a letter or _ and contain only alphanumeric characters, -, or _.
     */
    [jobId: JobId]: JobDefinition | RemoteJobDefinition
};