import csv
from pathlib import Path

import matplotlib

matplotlib.use("Agg")
import matplotlib.pyplot as plt


root = Path(__file__).resolve().parent
with (root / "results/results.csv").open(encoding="utf-8", newline="") as file:
    rows = list(csv.DictReader(file))

output = root / "docs/plots"
output.mkdir(parents=True, exist_ok=True)


def draw(ax, algorithms, input_case):
    for algorithm in algorithms:
        data = sorted(
            (row for row in rows if row["algorithm"] == algorithm and row["case"] == input_case),
            key=lambda row: int(row["n"]),
        )
        ax.plot(
            [int(row["n"]) for row in data],
            [float(row["average_ms"]) for row in data],
            marker="o",
            label=algorithm,
        )
    ax.set_ylabel("Average time (ms)")
    ax.set_ylim(bottom=0)
    ax.grid(alpha=0.3)
    ax.legend()


fig, ax = plt.subplots(figsize=(7, 4.5))
draw(ax, ["LCS", "EditDistance"], "random")
ax.set_title("Dynamic programming: equal-length input strings")
ax.set_xlabel("Length of each string")
fig.tight_layout()
fig.savefig(output / "dp_time.png", dpi=180)
plt.close(fig)

fig, axes = plt.subplots(1, 2, figsize=(11, 4.5))
for ax, input_case in zip(axes, ["present", "absent"]):
    draw(ax, ["Naive", "KMP", "RabinKarp"], input_case)
    ax.set_title(f"Pattern {input_case} (length 32)")
    ax.set_xlabel("Text length")
fig.tight_layout()
fig.savefig(output / "matching_time.png", dpi=180)
plt.close(fig)

fig, ax = plt.subplots(figsize=(7, 4.5))
draw(ax, ["Kasai"], "random")
ax.set_title("Kasai: suffix array supplied, construction excluded")
ax.set_xlabel("String length")
fig.tight_layout()
fig.savefig(output / "kasai_time.png", dpi=180)
plt.close(fig)
print("Saved 3 plots in docs/plots")
