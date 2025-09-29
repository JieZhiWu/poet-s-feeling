// @ts-ignore
/* eslint-disable */
import request from '@/libs/request';

export interface PoemGenerateRequest {
  userId?: number;
  inputText: string;
}

export interface PoemResponse {
  title: string;
  keywords: string[];
  poem: string;
  recommendations: string[];
}

export interface PoemWithId {
  id: number;
  poemResponse: PoemResponse;
}

export interface PoemAnalysisResponse {
  analysis: string;
  style: string;
  inspiration: string;
  relatedThemes: string[];
}

export interface BaseResponse<T> {
  code: number;
  data: T;
  message: string;
}

/** 生成诗歌 POST /api/poem/generate */
export async function generatePoemUsingPost(
  body: PoemGenerateRequest,
  options?: { [key: string]: any },
) {
  return request<BaseResponse<PoemWithId>>('/api/poem/generate', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}

/** 生成诗歌解读报告 POST /api/poem/${param0}/analysis */
export async function generateAnalysisUsingPost(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: {
    /** 诗歌ID */
    id: number;
  },
  options?: { [key: string]: any },
) {
  const { id: param0, ...queryParams } = params;
  return request<BaseResponse<PoemAnalysisResponse>>(`/api/poem/${param0}/analysis`, {
    method: 'POST',
    params: { ...queryParams },
    ...(options || {}),
  });
}

/** 获取诗歌详情 GET /api/poem/${param0} */
export async function getPoemByIdUsingGet(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: {
    /** 诗歌ID */
    id: number;
  },
  options?: { [key: string]: any },
) {
  const { id: param0, ...queryParams } = params;
  return request<BaseResponse<PoemResponse>>(`/api/poem/${param0}`, {
    method: 'GET',
    params: { ...queryParams },
    ...(options || {}),
  });
}
