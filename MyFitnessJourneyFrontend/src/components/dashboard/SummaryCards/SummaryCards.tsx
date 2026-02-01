import React from 'react';
import './SummaryCards.css';

interface SummaryCardProps {
  title: string;
  value: string | number;
  subtitle: string;
  icon: React.ReactNode;
}

const SummaryCard: React.FC<SummaryCardProps> = ({ title, value, subtitle, icon }) => {
  return (
    <div className="summary-card">
      <div className="card-header">
        <span className="card-title">{title}</span>
        {icon}
      </div>
      <div className="card-value">{value}</div>
      <div className="card-subtitle">{subtitle}</div>
    </div>
  );
};

interface SummaryCardsProps {
  workouts?: number;
  messages?: number;
  streak?: number;
}

export const SummaryCards: React.FC<SummaryCardsProps> = ({
  workouts = 0,
  messages = 0,
  streak = 0,
}) => {
  return (
    <div className="summary-cards">
      <SummaryCard
        title="WORKOUTS"
        value={workouts}
        subtitle="completed"
        icon={
          <svg className="card-icon" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
            <path d="M14.7 6.3a1 1 0 0 0 0 1.4l1.6 1.6a1 1 0 0 0 1.4 0l3.77-3.77a6 6 0 0 1-7.94 7.94l-6.91 6.91a2.12 2.12 0 0 1-3-3l6.91-6.91a6 6 0 0 1 7.94-7.94l-3.76 3.76z" strokeLinecap="round" strokeLinejoin="round"/>
          </svg>
        }
      />
      <SummaryCard
        title="MESSAGES"
        value={messages}
        subtitle="unread"
        icon={
          <svg className="card-icon" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
            <path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z" strokeLinecap="round" strokeLinejoin="round"/>
          </svg>
        }
      />
      <SummaryCard
        title="STREAK"
        value={streak}
        subtitle="days"
        icon={
          <svg className="card-icon" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
            <polyline points="22 12 18 12 15 21 9 3 6 12 2 12" strokeLinecap="round" strokeLinejoin="round"/>
          </svg>
        }
      />
    </div>
  );
};

