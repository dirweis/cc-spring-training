package de.training.model;

import java.net.URI;
import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonProperty;

public record Error(URI type, @NotNull @Size(min = 5, max = 50) String title, @NotNull URI instance,
		@Size(min = 10, max = 200) String detail,
		@JsonProperty("invalid_params") @Valid List<InvalidParam> invalidParams, @NotNull Long timestamp) {

	public record InvalidParam(@NotNull @Size(min = 3, max = 20) String name,
			@NotNull @Size(min = 8, max = 200) String reason) {
		/* Nothing special in this record */
	}
}
