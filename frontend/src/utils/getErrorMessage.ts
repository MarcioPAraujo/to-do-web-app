import { IBackendErrorResponse } from "@/interfaces/BackendErrorResponse";
import { AxiosError } from "axios";

export const isBackendError = (
  error: unknown,
): error is AxiosError<IBackendErrorResponse> => {
  if (!error || typeof error !== "object") {
    return false;
  }

  if (!("response" in error)) {
    return false;
  }

  if (typeof error.response !== "object" || error.response === null) {
    return false;
  }

  if (
    !("status" in error.response) ||
    typeof error.response.status !== "number"
  ) {
    return false;
  }

  return (
    error &&
    typeof error === "object" &&
    "response" in error &&
    typeof error.response === "object" &&
    "status" in error.response
  );
};

export const getErrorMessage = (error: unknown): string => {
  console.log(error);
  if (error instanceof Error) {
    return error.message;
  }
  if (isBackendError(error)) {
    return error?.message;
  }
  return "An unknown error has occured";
};
