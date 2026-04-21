export interface ITaskListingRequestParams {
  page: number;
  size: number;
  name?: string;
  completed?: boolean;
}
