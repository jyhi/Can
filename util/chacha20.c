// A quick-n-dirty software implementation of ChaCha20 block function mainly
// used for displaying, comparing and debugging the internal states.
//
// SPDX-FileCopyrightText: 2021 Minyong Li <ml10g20@soton.ac.uk>
// SPDX-License-Identifier: GPL-3.0-or-later

#include <stdio.h>
#include <stdlib.h>
#include <stdint.h>
#include <string.h>

#define DEBUG

typedef uint32_t word_t;

#define WORD_WORDS 1
#define WORD_BYTES sizeof(word_t)
#define WORD_BITS (WORD_BYTES * 8)

#define CONSTANTS_WORDS 4
#define KEY_WORDS 8
#define COUNTER_WORDS 1
#define NONCE_WORDS 3
#define STATE_WORDS 16

#define CONSTANTS_BYTES (CONSTANTS_WORDS * 4)
#define KEY_BYTES (KEY_WORDS * 4)
#define COUNTER_BYTES (COUNTER_WORDS * 4)
#define NONCE_BYTES (NONCE_WORDS * 4)
#define STATE_BYTES (STATE_WORDS * 4)

#define CONSTANTS_BITS (CONSTANTS_BYTES * 8)
#define KEY_BITS (KEY_BYTES * 8)
#define COUNTER_BITS (COUNTER_BYTES * 8)
#define NONCE_BITS (NONCE_BYTES * 8)
#define STATE_BITS (STATE_BYTES * 8)

#ifdef DEBUG
#define dbg(fmt, ...) fprintf(stderr, ">> %s: " fmt "\n", __func__, __VA_ARGS__)
#define eprintln_state(state) { \
  char *str = state_to_string(state); \
  fputs(str, stderr); \
  putchar('\n'); \
  free(str); \
}
#else
#define dbg(fmt, ...)
#define eprintln_state(state)
#endif

char *state_to_string(word_t *state) {
  size_t str_size = snprintf(NULL, 0,
    "%x %x %x %x\n%x %x %x %x\n%x %x %x %x\n%x %x %x %x",
    state[0], state[1], state[2], state[3],
    state[4], state[5], state[6], state[7],
    state[8], state[9], state[10], state[11],
    state[12], state[13], state[14], state[15]);

  char *str = malloc(str_size + 1);
  if (!str)
    return NULL;

  snprintf(str, str_size + 1,
    "%x %x %x %x\n%x %x %x %x\n%x %x %x %x\n%x %x %x %x",
    state[0], state[1], state[2], state[3],
    state[4], state[5], state[6], state[7],
    state[8], state[9], state[10], state[11],
    state[12], state[13], state[14], state[15]);

  return str;
}

word_t left_rotate(const word_t x, const int n) {
  return (x << n) | (x >> (WORD_BITS - n));
}

void quarter_round(word_t *a, word_t *b, word_t *c, word_t *d) {
  *a += *b;
  *d ^= *a;
  *d = left_rotate(*d, 16);
  *c += *d;
  *b ^= *c;
  *b = left_rotate(*b, 12);
  *a += *b;
  *d ^= *a;
  *d = left_rotate(*d, 8);
  *c += *d;
  *b ^= *c;
  *b = left_rotate(*b, 7);
}

void inner_block(word_t *state) {
  quarter_round(&state[0], &state[4], &state[8], &state[12]);
  quarter_round(&state[1], &state[5], &state[9], &state[13]);
  quarter_round(&state[2], &state[6], &state[10], &state[14]);
  quarter_round(&state[3], &state[7], &state[11], &state[15]);

  dbg("%s", "columnar round result");
  eprintln_state(state);

  quarter_round(&state[0], &state[5], &state[10], &state[15]);
  quarter_round(&state[1], &state[6], &state[11], &state[12]);
  quarter_round(&state[2], &state[7], &state[8], &state[13]);
  quarter_round(&state[3], &state[4], &state[9], &state[14]);

  dbg("%s", "diagonal round result");
  eprintln_state(state);
}

word_t *state_add(word_t *state_a, word_t *state_b) {
  word_t *state_y = malloc(STATE_BYTES);
  if (!state_y)
    return NULL;

  for (size_t i = 0; i < STATE_WORDS; i++) {
    state_y[i] = state_a[i] + state_b[i];
  }

  return state_y;
}

word_t *chacha20_block(const unsigned char *key, const unsigned char *counter, const unsigned char *nonce) {
  static const word_t constants[] = {
    0x61707865, 0x3320646e, 0x79622d32, 0x6b206574
  };

  word_t *state = malloc(STATE_BYTES);
  if (!state)
    return NULL;

  dbg("%s", "initializing block");

  memcpy(state, constants, CONSTANTS_BYTES);
  memcpy(state + CONSTANTS_WORDS, key, KEY_BYTES);
  memcpy(state + CONSTANTS_WORDS + KEY_WORDS, counter, COUNTER_BYTES);
  memcpy(state + CONSTANTS_WORDS + KEY_WORDS + COUNTER_WORDS, nonce, NONCE_BYTES);

  dbg("%s", "initialized block");
  eprintln_state(state);

  word_t *initial_state = malloc(STATE_BYTES);
  if (!initial_state) {
    free(state);
    return NULL;
  }

  memcpy(initial_state, state, STATE_BYTES);

  dbg("%s", "initial state copied");

  for (size_t i = 0; i < 10; i++) {
    dbg("%s %zu", "inner block iteration", i);
    inner_block(state);
  }

  dbg("%s", "adding final state");

  word_t *final_state = state_add(state, initial_state);

  dbg("%s", "final state produced");
  eprintln_state(final_state);

  free(initial_state);
  free(state);

  return final_state;
}

int main(void) {
  static const unsigned char key[KEY_BYTES] = {0};
  static const unsigned char counter[COUNTER_BYTES] = {0};
  static const unsigned char nonce[NONCE_BYTES] = {0};

  word_t *block = chacha20_block(key, counter, nonce);

  char *str = state_to_string(block);
  printf("The final state: %s\n", str);

  free(str);
  free(block);

  return 0;
}
