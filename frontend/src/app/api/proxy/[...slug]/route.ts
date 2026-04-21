import { IBackendErrorResponse } from "@/interfaces/BackendErrorResponse";
import { baseUrl } from "@/services/api";
import { isBackendError } from "@/utils/getErrorMessage";
import axios, { isAxiosError } from "axios";
import { NextResponse } from "next/server";

interface IProxyRequestParams {
  params: Promise<{ slug: string[] }>;
}

const handleErrorResponse = (
  error: unknown,
): NextResponse<IBackendErrorResponse> => {
  let errorResponse: IBackendErrorResponse = {
    status: 500,
    error: "Internal Server Error",
    message: "An error occurred while processing the request",
    timestamp: new Date().toISOString(),
  };
  if (isAxiosError(error)) {
    errorResponse = {
      status: error.response?.status || 500,
      error: error.response?.statusText || "Error",
      message: error.response?.data?.message,
      timestamp: new Date().toISOString(),
    };
  }
  // backend error
  if (isBackendError(error)) {
    errorResponse = {
      status: error.response?.status || 500,
      error: error.response?.statusText || "Error",
      message: error.response?.data?.message || "An error occurred",
      timestamp: new Date().toISOString(),
    };
  }

  return new NextResponse<IBackendErrorResponse>(
    JSON.stringify(errorResponse),
    {
      status: errorResponse.status,
    },
  );
};

export async function handleProxyRequest(
  request: Request,
  { params }: IProxyRequestParams,
) {
  const { slug } = await params;

  const path = slug.join("/");
  const { search } = new URL(request.url);

  const body = request.method !== "GET" ? await request.text() : undefined;

  const finalUrl = `${baseUrl}${path}${search}`;

  try {
    const response = await axios(finalUrl, {
      method: request.method,
      headers: {
        "Content-Type": "application/json",
        Authorization: request.headers.get("Authorization") || "",
      },
      data: body,
    });

    return new NextResponse(JSON.stringify(response.data), {
      status: response.status,
    });
  } catch (error) {
    return handleErrorResponse(error);
  }
}

export const GET = handleProxyRequest;
export const POST = handleProxyRequest;
export const PUT = handleProxyRequest;
export const DELETE = handleProxyRequest;
