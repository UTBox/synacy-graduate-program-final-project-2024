package com.synacy.graduate.program.leaveapp.leave_management.web;

import java.util.List;

public record PageResponse<T>(long totalCount, int pageNumber, List<T> content) {
}
