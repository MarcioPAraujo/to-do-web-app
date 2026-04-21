import { ITaskListingRequestParams } from "@/interfaces/tasks/TaskListingRequestParams";
import { ITaskListingResponse } from "@/interfaces/tasks/TasksListingResponse";
import { api } from "./api";

export const getTasksListService = async (
  params: ITaskListingRequestParams,
): Promise<ITaskListingResponse> => {
  const fetchUrl = `/api/tasks`;

  const response = await api.get<ITaskListingResponse>(fetchUrl, { params });

  return response.data;
};
