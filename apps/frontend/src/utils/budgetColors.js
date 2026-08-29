/**
 * Standardized Utility for Budget Status & Colors
 *
 * Thresholds (Needs / Wants):
 * - < 80%: safe (emerald green)
 * - 80% - 99%: caution (amber yellow)
 * - 100% - 114%: warning (orange)
 * - >= 115%: danger (red)
 */
export const getBudgetStatus = (spent, budgeted) => {
  if (budgeted <= 0) {
    return {
      color: 'gray',
      label: 'No Budget',
      bg: 'bg-slate-600',
      text: 'text-slate-400',
      colorClass: 'bg-slate-600',
      textClass: 'text-slate-400',
      hex: '#64748B',
    };
  }
  const pct = (spent / budgeted) * 100;
  if (pct < 80) {
    return {
      color: 'safe',
      label: 'Under Budget',
      detailLabel: `Under Budget — $${(budgeted - spent).toFixed(0)} left`,
      bg: 'bg-emerald-500',
      text: 'text-emerald-400',
      colorClass: 'bg-emerald-500',
      textClass: 'text-emerald-400',
      hex: '#10B981',
    };
  }
  if (pct < 100) {
    return {
      color: 'caution',
      label: 'Near Limit',
      detailLabel: `Nearing Budget Limit — $${(budgeted - spent).toFixed(0)} left`,
      bg: 'bg-amber-500',
      text: 'text-amber-400',
      colorClass: 'bg-amber-500',
      textClass: 'text-amber-400',
      hex: '#F59E0B',
    };
  }
  if (pct < 115) {
    return {
      color: 'warning',
      label: 'Over Budget',
      detailLabel: `Over Budget by $${(spent - budgeted).toFixed(0)}`,
      bg: 'bg-orange-500',
      text: 'text-orange-400',
      colorClass: 'bg-orange-500',
      textClass: 'text-orange-400',
      hex: '#F97316',
    };
  }
  return {
    color: 'danger',
    label: 'Exceeded',
    detailLabel: `Exceeded budget by $${(spent - budgeted).toFixed(0)}`,
    bg: 'bg-red-500',
    text: 'text-red-400',
    colorClass: 'bg-red-500',
    textClass: 'text-red-400',
    hex: '#EF4444',
  };
};


