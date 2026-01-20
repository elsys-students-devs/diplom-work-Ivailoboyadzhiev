import React, { useState, useEffect } from 'react';
import { getCurrentUser, UserDto } from '../services/authService';
import { HamburgerMenu } from '../components/common/HamburgerMenu';
import { DropdownMenu } from '../components/common/DropdownMenu';
import { Loading } from '../components/common/Loading';
import { ChatHeader } from '../components/chat/ChatHeader';
import { ChatMain } from '../components/chat/ChatMain';
import './Chat.css';

const Chat: React.FC = () => {
  const [user, setUser] = useState<UserDto | null>(null);
  const [loading, setLoading] = useState(true);
  const [menuOpen, setMenuOpen] = useState(false);

  useEffect(() => {
    const fetchUser = async () => {
      try {
        const currentUser = await getCurrentUser();
        setUser(currentUser);
      } catch (error) {
        console.error('Failed to fetch user:', error);
      } finally {
        setLoading(false);
      }
    };

    fetchUser();
  }, []);

  if (loading) {
    return (
      <div className="chat-container">
        <Loading message="Зареждане..." />
      </div>
    );
  }

  if (!user) {
    return (
      <div className="chat-container">
        <div className="chat-error">
          <p>Не може да се зареди потребителят. Моля, влезте отново.</p>
        </div>
      </div>
    );
  }

  return (
    <div className="chat-container">
      <HamburgerMenu open={menuOpen} onToggle={() => setMenuOpen(!menuOpen)} />
      <DropdownMenu open={menuOpen} onClose={() => setMenuOpen(false)} />

      <ChatHeader 
        title="Съобщения" 
        subtitle="Разговаряйте с други потребители"
      />

      <ChatMain currentUserId={user.id} />
    </div>
  );
};

export default Chat;
